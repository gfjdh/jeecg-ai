package org.jeecg.modules.course.course.component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Arrays;
import java.util.Collections;
import jakarta.annotation.PostConstruct;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

    public static final String GLOBAL_QUEUE_KEY = "course:rush:global_queue";
    public static final String STATUS_KEY_PREFIX = "course:rush:status:";
    public static final String BLOOM_FILTER_KEY_PREFIX = "course:rush:bloom:";
    public static final String FULL_KEY_PREFIX = "course:rush:full:";

    @PostConstruct
    public void startConsumer() {
        // 初始化布隆过滤器，加载数据库中已有的选课数据
        initBloomFilter();

        new Thread(() -> {
            BatchStats currentStats = null;
            while (true) {
                try {
                    // 阻塞式弹出, 改为10秒超时
                    Object payloadObj = redisTemplate.opsForList().rightPop(GLOBAL_QUEUE_KEY, 10, TimeUnit.SECONDS);
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
        long methodStart = System.currentTimeMillis();
        stats.totalRequests.incrementAndGet();

        // 负载格式: "courseId:studentNo"
        String[] parts = payload.split(":");
        if (parts.length != 2) return;
        String courseId = parts[0];
        String studentNo = parts[1];
        String statusKey = STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        String countKey = "course:rush:count:" + courseId;

        // 记录各步骤的耗时，用于瓶颈分析（初始值-1表示未完成）
        AtomicLong cost2 = new AtomicLong(-1);
        AtomicLong cost3 = new AtomicLong(-1);
        AtomicLong cost4 = new AtomicLong(-1);
        AtomicLong cost5 = new AtomicLong(-1);

        try {
            // 1. 获取课程容量和类型（在入队列时已带缓存，对性能影响较小）
            long s1 = System.currentTimeMillis();
            TeacherCourse teacherCourse = teacherCourseService.getTeacherCourse(courseId);
            stats.step1Cost.addAndGet(System.currentTimeMillis() - s1);

            // 初始化异步控制
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            AtomicInteger passCount = new AtomicInteger(0);
            
            // 2. 异步检查是否已选该课程 (使用布隆过滤器优化)
            CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
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
                    long cost = System.currentTimeMillis() - start;
                    stats.step2Cost.addAndGet(cost);
                    cost2.set(cost);
                }
            });

            // 3. 异步检查课程人数是否已满
            CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
                try {
                    String fullKey = FULL_KEY_PREFIX + courseId;
                    String isFullStr = stringRedisTemplate.opsForValue().get(fullKey);
                    if (isFullStr != null && Boolean.parseBoolean(isFullStr)) {
                        return "FAILED: 课程已满";
                    }

                    long selectedCount = studentCourseSelectionService.count(new LambdaQueryWrapper<StudentCourseSelection>()
                            .eq(StudentCourseSelection::getCourseId, courseId));
                    if (selectedCount >= teacherCourse.getCapacity()) {
                        stringRedisTemplate.opsForValue().set(fullKey, "true", 10, TimeUnit.SECONDS);
                        return "FAILED: 课程已满";
                    }
                    return null;
                } finally {
                    long cost = System.currentTimeMillis() - start;
                    stats.step3Cost.addAndGet(cost);
                    cost3.set(cost);
                }
            });

            // 4. 异步检查选课时间冲突（通过 SQL 处理冲突检验逻辑）
            CompletableFuture<String> task4 = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
                try {
                    String conflictCourseId = studentCourseSelectionService.checkTimeConflict(studentNo, courseId);
                    if (conflictCourseId != null) {
                        return "FAILED: 与已选课程时间冲突： " + conflictCourseId;
                    }
                    return null;
                } finally {
                    long cost = System.currentTimeMillis() - start;
                    stats.step4Cost.addAndGet(cost);
                    cost4.set(cost);
                }
            });

            // 5. 异步确定课程类型
            CompletableFuture<Integer> task5 = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
                try {
                    Integer finalCourseType = teacherCourse.getCourseType();
                    Integer overrideType = studentCourseSelectionService.getOverrideCourseType(studentNo, courseId);
                    if (overrideType != null) {
                        finalCourseType = overrideType;
                    }
                    return finalCourseType;
                } finally {
                    long cost = System.currentTimeMillis() - start;
                    stats.step5Cost.addAndGet(cost);
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
            long waitStart = System.currentTimeMillis();
            String validationResult;
            try {
                validationResult = resultFuture.get(30, TimeUnit.SECONDS);
                stats.waitCost.addAndGet(System.currentTimeMillis() - waitStart);
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
            long s6 = System.currentTimeMillis();
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
            
            stringRedisTemplate.opsForValue().set(statusKey, "SUCCESS: 选课成功", 5, TimeUnit.MINUTES);
            stats.step6Cost.addAndGet(System.currentTimeMillis() - s6);
            stats.successCount.incrementAndGet();

        } catch (Exception e) {
            log.error("处理抢课请求时出错: " + payload, e);
            stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 系统错误", 5, TimeUnit.MINUTES);
            stats.failCount.incrementAndGet();
        } finally {
            // 计算瓶颈：只要有步骤完成（耗时>=0），就参与比较，记录耗时最长的步骤
            long c2 = cost2.get();
            long c3 = cost3.get();
            long c4 = cost4.get();
            long c5 = cost5.get();
            
            long max = -1;
            if (c2 > max) max = c2;
            if (c3 > max) max = c3;
            if (c4 > max) max = c4;
            if (c5 > max) max = c5;

            if (max > -1) {
                if (max == c2) stats.step2Bottleneck.incrementAndGet();
                else if (max == c3) stats.step3Bottleneck.incrementAndGet();
                else if (max == c4) stats.step4Bottleneck.incrementAndGet();
                else if (max == c5) stats.step5Bottleneck.incrementAndGet();
            }

            stringRedisTemplate.opsForValue().decrement(countKey);
            stats.totalCost.addAndGet(System.currentTimeMillis() - methodStart);
        }
    }

    /**
     * 判断是否可能已选（布隆过滤器）
     */
    private boolean isMember(String key, String value) {
        for (int i = 0; i < 3; i++) {
            long offset = (Objects.hash(value, i) & Integer.MAX_VALUE) % 1000000;
            if (!Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, offset))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加到布隆过滤器
     */
    private void addToBloomFilter(String key, String value) {
        for (int i = 0; i < 3; i++) {
            long offset = (Objects.hash(value, i) & Integer.MAX_VALUE) % 1000000;
            stringRedisTemplate.opsForValue().setBit(key, offset, true);
        }
    }

    /**
     * 初始化布隆过滤器，加载数据库中所有课程的已有选课数据
     */
    public void initBloomFilter() {
        log.info("开始初始化布隆过滤器...");
        // 查询数据库中所有已选课记录
        List<StudentCourseSelection> allSelections = studentCourseSelectionService.list();

        if (allSelections != null && !allSelections.isEmpty()) {
            for (StudentCourseSelection selection : allSelections) {
                String bloomKey = BLOOM_FILTER_KEY_PREFIX + selection.getCourseId();
                addToBloomFilter(bloomKey, selection.getStudentNo());
            }
        }
        log.info("布隆过滤器初始化完成，共加载 {} 条选课数据", allSelections == null ? 0 : allSelections.size());
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