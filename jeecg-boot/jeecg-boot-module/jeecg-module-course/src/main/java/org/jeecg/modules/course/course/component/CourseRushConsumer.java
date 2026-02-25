package org.jeecg.modules.course.course.component;

import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.service.IClassTimeService;
import java.util.Set;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Arrays;
import java.util.Map;
import java.util.BitSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import jakarta.annotation.PostConstruct;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Slf4j
@Component
public class CourseRushConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private IStudentCourseSelectionService studentCourseSelectionService;
    
    @Autowired
    private ITeacherCourseService teacherCourseService;

    @Autowired
    private IClassTimeService classTimeService;

    private final Map<String, BloomFilterContainer> localBloomFilters = new ConcurrentHashMap<>();

    // 自定义链表节点实现队列
    protected static class Node<E> {
        E item;
        Node<E> next;
        Node(E x) { 
            item = x; 
            next = null;
        }
    }

    public static class MyLinkedQueue<E> {
        private final AtomicInteger count = new AtomicInteger(0);
        private Node<E> head;
        private Node<E> tail;
        
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();

        public MyLinkedQueue() {
            head = new Node<E>(null);
            tail = head;
        }

        // 入队操作
        public boolean offer(E e) {
            if (e == null) throw new NullPointerException();
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                Node<E> node = new Node<>(e);
                tail.next = node;
                tail = node;
                // 增加计数并唤醒等待的消费者
                count.incrementAndGet();
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
            return true;
        }

        // 出队操作
        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                while (count.get() == 0) {
                    if (nanos <= 0)
                        return null;
                    nanos = notEmpty.awaitNanos(nanos);
                }
                
                Node<E> first = head.next;
                E x = first.item;
                first.item = null;
                head = first;
                
                count.decrementAndGet();
                return x;
            } finally {
                lock.unlock();
            }
        }
        
        public int size() {
            return count.get();
        }
    }

    // 改用自定义队列实现
    public static final MyLinkedQueue<String> rushQueue = new MyLinkedQueue<>();

    public static final String GLOBAL_QUEUE_KEY = "course:rush:global_queue";
    public static final String STATUS_KEY_PREFIX = "course:rush:status:";
    public static final String BLOOM_FILTER_KEY_PREFIX = "course:rush:bloom:";
    public static final String FULL_KEY_PREFIX = "course:rush:full:";
    public static final String REMAIN_KEY_PREFIX = "course:rush:remain:";

    @PostConstruct
    public void startConsumer() {
        // 初始化布隆过滤器，加载数据库中已有的选课数据
        initBloomFilter();

        // 启动队列长度实时监控线程 (使用 \r 同一行覆盖打印，避免刷屏)
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int size = rushQueue.size();
                    // \r 将光标移至行首，末尾补空格确保覆盖旧内容的残余字符
                    System.out.print("\r>>> [队列实时监控] 当前待处理长度: " + size + "    ");
                    System.out.flush();
                    TimeUnit.MILLISECONDS.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    // 监控异常不影响抢课主逻辑
                }
            }
        }, "QueueMonitorThread").start();

        new Thread(() -> {
            BatchStats currentStats = null;
            while (true) {
                try {
                    // 阻塞式弹出, 改为10秒超时
                    // Object payloadObj = redisTemplate.opsForList().rightPop(GLOBAL_QUEUE_KEY, 10, TimeUnit.SECONDS);
                    Object payloadObj = rushQueue.poll(10, TimeUnit.SECONDS);
                    if (payloadObj != null) {
                        if (currentStats == null) {
                            String newBatchId = String.valueOf(System.currentTimeMillis());
                            log.info("开始新的选课监控周期: {}", newBatchId);
                            currentStats = new BatchStats(newBatchId);
                        }
                        handleRush((String) payloadObj, currentStats);
                        // 处理完一个后继续循环，立即尝试获取下一个
                        continue;
                    } else {
                        // 超过10秒没有新请求，结束当前周期
                        if (currentStats != null) {
                            currentStats.printSummary();
                            currentStats = null;
                        }
                    }
                } catch (Exception e) {
                    log.error("抢课消费者错误", e);
                    // 发生异常时适当休眠
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }).start();
    }


    private void handleRush(String payload, BatchStats stats) {
        long methodStart = System.nanoTime();
        stats.totalRequests.incrementAndGet();

        // 负载格式: "courseId:studentNo"
        String[] parts = payload.split(":");
        if (parts.length != 2) return;
        String courseId = parts[0];
        String studentNo = parts[1];
        String statusKey = STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        String countKey = "course:rush:count:" + courseId;

        // 记录各步骤的耗时，用于瓶颈分析（初始值-1表示未完成）
        // 改用纳秒记录，避免 0ms 引起的统计偏差
        AtomicLong cost2 = new AtomicLong(-1);
        AtomicLong cost3 = new AtomicLong(-1);
        AtomicLong cost4 = new AtomicLong(-1);
        AtomicLong cost5 = new AtomicLong(-1);

        boolean hasDeductedRedis = false; // 标记是否已经扣减 Redis 库存，异常时需要回滚
        try {
            // 1. 获取课程容量和类型（在入队列时已带缓存，对性能影响较小）
            long s1 = System.nanoTime();
            TeacherCourse teacherCourse = teacherCourseService.getTeacherCourse(courseId);
            stats.step1Cost.addAndGet((System.nanoTime() - s1) / 1000000); // 纳秒转毫秒累计

            // 初始化异步控制
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            AtomicInteger passCount = new AtomicInteger(0);
            
            // 2. 异步检查是否已选该课程 (使用布隆过滤器优化)
            CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
                long start = System.nanoTime();
                try {
                    String bloomKey = BLOOM_FILTER_KEY_PREFIX + courseId;
                    if (isMember(bloomKey, studentNo)) {
                        // 如果布隆过滤器判断可能已选，再查库确认（排除误判）
                        boolean exists = studentCourseSelectionService.exists(new LambdaQueryWrapper<StudentCourseSelection>()
                                .eq(StudentCourseSelection::getCourseId, courseId)
                                .eq(StudentCourseSelection::getStudentNo, studentNo));
                        if (exists) {
                            return "SUCCESS: 已选该课程";
                        }
                    }
                    return null;
                } finally {
                    long cost = System.nanoTime() - start;
                    stats.step2Cost.addAndGet(cost / 1000000);
                    cost2.set(cost);
                }
            });

            // 3. 异步检查课程人数是否已满 (使用 Redis 剩余容量缓存优化)
            CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
                long start = System.nanoTime();
                try {
                    String remainKey = REMAIN_KEY_PREFIX + courseId;
                    String remainStr = stringRedisTemplate.opsForValue().get(remainKey);
                    
                    boolean needLoadFromDb = (remainStr == null);
                    
                    // 如果 Redis 显示没库存了(<=1)，为防止 Redis 数据偏差导致"假满"，强制查库校准
                    if (!needLoadFromDb && Long.parseLong(remainStr) <= 1) {
                        needLoadFromDb = true;
                    }

                    if (needLoadFromDb) {
                        // 如果过期或不存在，或者Redis显示已满，从数据库重新加载
                        long selectedCount = studentCourseSelectionService.count(new LambdaQueryWrapper<StudentCourseSelection>()
                                .eq(StudentCourseSelection::getCourseId, courseId));
                        long remainValue = teacherCourse.getCapacity() - selectedCount;
                        // 写入 Redis，有效期 5 秒
                        stringRedisTemplate.opsForValue().set(remainKey, String.valueOf(remainValue), 5, TimeUnit.SECONDS);
                        
                        if (remainValue <= 0) {
                            return "FAILED: 课程已满";
                        }
                    }
                    return null;
                } finally {
                    long cost = System.nanoTime() - start;
                    stats.step3Cost.addAndGet(cost / 1000000);
                    cost3.set(cost);
                }
            });

            // 4. 异步检查选课时间冲突
            CompletableFuture<String> task4 = CompletableFuture.supplyAsync(() -> {
                long start = System.nanoTime();
                try {
                    String conflictCourseId = studentCourseSelectionService.checkTimeConflict(studentNo, courseId);
                    if (conflictCourseId != null) {
                        return "FAILED: 与已选课程时间冲突： " + conflictCourseId;
                    }
                    return null;
                } finally {
                    long cost = System.nanoTime() - start;
                    stats.step4Cost.addAndGet(cost / 1000000);
                    cost4.set(cost);
                }
            });

            // 5. 异步确定课程类型
            CompletableFuture<Integer> task5 = CompletableFuture.supplyAsync(() -> {
                long start = System.nanoTime();
                try {
                    Integer finalCourseType = teacherCourse.getCourseType();
                    Integer overrideType = studentCourseSelectionService.getOverrideCourseType(studentNo, courseId);
                    if (overrideType != null) {
                        finalCourseType = overrideType;
                    }
                    return finalCourseType;
                } finally {
                    long cost = System.nanoTime() - start;
                    stats.step5Cost.addAndGet(cost / 1000000);
                    cost5.set(cost);
                }
            });

            List<CompletableFuture<String>> validationTasks = Arrays.asList(task2, task3, task4);

            // 结果处理器：若有任务返回非空结果（失败或已存在），则完成主Future；若都通过（null），则计数完成后通知
            java.util.function.Consumer<String> resultHandler = (res) -> {
                if (res != null) {
                    resultFuture.complete(res);
                } else {
                    if (passCount.incrementAndGet() == validationTasks.size()) {
                        resultFuture.complete(null);
                    }
                }
            };
            
            // 注册回调
            validationTasks.forEach(task -> task.thenAccept(resultHandler));

            // 等待校验结果
            long waitStart = System.nanoTime();
            String validationResult;
            try {
                validationResult = resultFuture.get(30, TimeUnit.SECONDS);
                stats.waitCost.addAndGet((System.nanoTime() - waitStart) / 1000000);
            } catch (java.util.concurrent.TimeoutException e) {
                log.warn("选课校验超时: {}", payload);
                // 超时取消所有任务
                validationTasks.forEach(t -> t.cancel(true));
                task5.cancel(true);
                stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 请求超时，请重试", 5, TimeUnit.MINUTES);
                stats.failCount.incrementAndGet();
                return;
            }

            if (validationResult != null) {
                // 如果结果不为null，说明有了结论（失败或直接成功）
                stringRedisTemplate.opsForValue().set(statusKey, validationResult, 5, TimeUnit.MINUTES);
                
                // 尝试取消其他任务
                validationTasks.forEach(t -> { if(!t.isDone()) t.cancel(true); });
                if(!task5.isDone()) task5.cancel(true);

                if (validationResult.startsWith("SUCCESS")) {
                   stats.successCount.incrementAndGet();
                } else {
                   stats.failCount.incrementAndGet();
                }
                return;
            }

            // 6. 抢课成功 - 保存选课记录
            String remainKey = REMAIN_KEY_PREFIX + courseId;
            stringRedisTemplate.opsForValue().decrement(remainKey);
            hasDeductedRedis = true;

            long s6 = System.nanoTime();
            Integer finalCourseType = task5.get();

            StudentCourseSelection newSelection = new StudentCourseSelection();
            newSelection.setStudentNo(studentNo);
            newSelection.setCourseId(courseId);
            newSelection.setCourseCredit(teacherCourse.getCourseCredit()); 
            newSelection.setCourseType(finalCourseType);
            newSelection.setStudyStatus(0); // 0: 正常/在读状态
            studentCourseSelectionService.save(newSelection);
            
            // 抢课成功，更新布隆过滤器
            String bloomKey = BLOOM_FILTER_KEY_PREFIX + courseId;
            addToBloomFilter(bloomKey, studentNo);

            // 同步操作缓存：如果缓存存在，将新课程的时间加入缓存
            String studentTimeKey = "course:student:time:" + studentNo;
            @SuppressWarnings("unchecked")
            Set<String> selectedTimes = (Set<String>) redisTemplate.opsForValue().get(studentTimeKey);
            if (selectedTimes != null) {
                List<ClassTime> newCourseTimes = classTimeService.selectByMainId(courseId);
                if (newCourseTimes != null) {
                    for (ClassTime ct : newCourseTimes) {
                        if (ct.getWeekday() != null && ct.getStartSection() != null && ct.getEndSection() != null) {
                            for (int i = ct.getStartSection(); i <= ct.getEndSection(); i++) {
                                selectedTimes.add(ct.getWeekday() + "-" + i);
                            }
                        }
                    }
                    redisTemplate.opsForValue().set(studentTimeKey, selectedTimes, 10, TimeUnit.MINUTES);
                }
            }

            stringRedisTemplate.opsForValue().set(statusKey, "SUCCESS: 选课成功", 5, TimeUnit.MINUTES);
            stats.step6Cost.addAndGet((System.nanoTime() - s6) / 1000000);
            stats.successCount.incrementAndGet();

        } catch (Exception e) {
            log.error("处理抢课请求时出错: " + payload, e);
            stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 系统错误", 5, TimeUnit.MINUTES);
            stats.failCount.incrementAndGet();
            
            // 异常时回滚redis库存
            if (hasDeductedRedis) {
                 try {
                     stringRedisTemplate.opsForValue().increment(REMAIN_KEY_PREFIX + courseId);
                 } catch (Exception redisEx) {
                     log.error("回滚 Redis 库存失败: " + courseId, redisEx);
                 }
            }
            
        } finally {
            // 计算瓶颈：只要有步骤完成（耗时>=0），就参与比较，记录耗时最长的步骤
            long c2 = cost2.get();
            long c3 = cost3.get();
            long c4 = cost4.get();
            long c5 = cost5.get();
            
            // 只有当所有任务都真正完成后再比较，避免短路造成的误判
            boolean allFinished = c2 >= 0 && c3 >= 0 && c4 >= 0 && c5 >= 0;

            if (allFinished) {
                long max = -1;
                if (c2 > max) max = c2;
                if (c3 > max) max = c3;
                if (c4 > max) max = c4;
                if (c5 > max) max = c5;

                if (max > -1) {
                    // 使用纳秒级比较，通常不会相等，如果相等则可以忽略极小的差别
                    if (max == c2) stats.step2Bottleneck.incrementAndGet();
                    else if (max == c3) stats.step3Bottleneck.incrementAndGet();
                    else if (max == c4) stats.step4Bottleneck.incrementAndGet();
                    else if (max == c5) stats.step5Bottleneck.incrementAndGet();
                }
            }

            stringRedisTemplate.opsForValue().decrement(countKey);

            stats.totalCost.addAndGet((System.nanoTime() - methodStart) / 1000000);
        }
    }

    /**
     * 判断是否可能已选（布隆过滤器）
     * 优化：使用双重哈希增加散列度，使用读锁减少竞争
     */
    private boolean isMember(String key, String value) {
        BloomFilterContainer container = localBloomFilters.get(key);
        if (container == null) {
            return false;
        }
        return container.mightContain(value);
    }

    /**
     * 添加到布隆过滤器
     * 优化：使用写锁保证线程安全
     */
    private void addToBloomFilter(String key, String value) {
        // computeIfAbsent 保证容器本身的存在
        BloomFilterContainer container = localBloomFilters.computeIfAbsent(key, k -> new BloomFilterContainer());
        container.put(value);
    }

    /**
     * 初始化布隆过滤器，加载数据库中所有课程的已有选课数据
     */
    public void initBloomFilter() {
        log.info("开始初始化本地布隆过滤器...");
        localBloomFilters.clear();
        // 查询数据库中所有已选课记录
        List<StudentCourseSelection> allSelections = studentCourseSelectionService.list();

        if (allSelections != null && !allSelections.isEmpty()) {
            for (StudentCourseSelection selection : allSelections) {
                // 原有的逻辑 key 带有前缀，保持一致
                String bloomKey = BLOOM_FILTER_KEY_PREFIX + selection.getCourseId();
                // 使用 computeIfAbsent 确保容器只被创建一次
                BloomFilterContainer container = localBloomFilters.computeIfAbsent(bloomKey, k -> new BloomFilterContainer());
                container.put(selection.getStudentNo());
            }
        }
        log.info("本地布隆过滤器初始化完成，共加载 {} 条选课数据", allSelections == null ? 0 : allSelections.size());
    }

    // 内部类封装布隆过滤器逻辑
    private static class BloomFilterContainer {
        // 位数组大小，根据最大课程容量预估调整
        private static final int DEFAULT_SIZE = 100000;
        // 哈希函数数量
        private static final int HASH_COUNT = 3;
        
        private final BitSet bitSet;
        private final int size;
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public BloomFilterContainer() {
            // 默认值
            this(DEFAULT_SIZE);
        }

        public BloomFilterContainer(int size) {
            this.size = size;
            this.bitSet = new BitSet(size);
        }

        public void put(String value) {
            lock.writeLock().lock();
            try {
                int hash1 = value.hashCode();
                int hash2 = hash1 >>> 16;
                for (int i = 1; i <= HASH_COUNT; i++) {
                    int combinedHash = hash1 + (i * hash2);
                    if (combinedHash < 0) {
                        combinedHash = ~combinedHash;
                    }
                    bitSet.set(combinedHash % size);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public boolean mightContain(String value) {
            lock.readLock().lock();
            try {
                int hash1 = value.hashCode();
                int hash2 = hash1 >>> 16;
                for (int i = 1; i <= HASH_COUNT; i++) {
                    int combinedHash = hash1 + (i * hash2);
                    if (combinedHash < 0) {
                        combinedHash = ~combinedHash;
                    }
                    if (!bitSet.get(combinedHash % size)) {
                        return false;
                    }
                }
                return true;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    private static class BatchStats {
        String batchId;
        long startTime;
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicLong totalCost = new AtomicLong(0);

        // 各步骤总耗时
        AtomicLong step1Cost = new AtomicLong(0);
        AtomicLong step2Cost = new AtomicLong(0);
        AtomicLong step3Cost = new AtomicLong(0);
        AtomicLong step4Cost = new AtomicLong(0);
        AtomicLong step5Cost = new AtomicLong(0);
        AtomicLong step6Cost = new AtomicLong(0);
        AtomicLong waitCost = new AtomicLong(0);

        AtomicInteger step2Bottleneck = new AtomicInteger(0);
        AtomicInteger step3Bottleneck = new AtomicInteger(0);
        AtomicInteger step4Bottleneck = new AtomicInteger(0);
        AtomicInteger step5Bottleneck = new AtomicInteger(0);

        public BatchStats(String batchId) {
            this.batchId = batchId;
            this.startTime = System.currentTimeMillis();
        }

        public void printSummary() {
            long duration = System.currentTimeMillis() - startTime;
            int total = totalRequests.get();
            if (total == 0) return;

            StringBuilder sb = new StringBuilder();
            sb.append("\n================ 选课周期统计报告 ================\n");
            sb.append(String.format("批次ID: %s\n", batchId));
            sb.append(String.format("周期持续: %d ms\n", duration));
            sb.append(String.format("处理请求: %d (成功: %d, 失败: %d)\n", total, successCount.get(), failCount.get()));
            double avgTotal = totalCost.get() / (double) total;
            sb.append(String.format("平均总耗时: %.2f ms/req\n", avgTotal));
            sb.append("----------------各步骤平均耗时----------------\n");
            sb.append(String.format("Step1(GetCourse): %.2f ms\n", step1Cost.get() / (double) total));
            sb.append(String.format("Step2(Bloom)    : %.2f ms\n", step2Cost.get() / (double) total));
            sb.append(String.format("Step3(FullCheck): %.2f ms\n", step3Cost.get() / (double) total));
            sb.append(String.format("Step4(Conflict) : %.2f ms\n", step4Cost.get() / (double) total));
            sb.append(String.format("Step5(Type)     : %.2f ms\n", step5Cost.get() / (double) total));
            sb.append(String.format("WaitValid       : %.2f ms\n", waitCost.get() / (double) total));
            sb.append(String.format("Step6(Save)     : %.2f ms\n", step6Cost.get() / (double) total));
            
            sb.append("----------------各步骤瓶颈次数----------------\n");
            sb.append(String.format("Step2(Bloom)    : %d\n", step2Bottleneck.get()));
            sb.append(String.format("Step3(FullCheck): %d\n", step3Bottleneck.get()));
            sb.append(String.format("Step4(Conflict) : %d\n", step4Bottleneck.get()));
            sb.append(String.format("Step5(Type)     : %d\n", step5Bottleneck.get()));
            
            sb.append("==================================================");
            log.info(sb.toString());
        }
    }
}