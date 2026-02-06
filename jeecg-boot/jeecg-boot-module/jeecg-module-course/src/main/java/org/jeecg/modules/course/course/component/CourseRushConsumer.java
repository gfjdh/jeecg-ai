package org.jeecg.modules.course.course.component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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

    public static final String GLOBAL_QUEUE_KEY = "course:rush:global_queue";
    public static final String STATUS_KEY_PREFIX = "course:rush:status:";
    public static final String BLOOM_FILTER_KEY_PREFIX = "course:rush:bloom:";

    @PostConstruct
    public void startConsumer() {
        // 初始化布隆过滤器，加载数据库中已有的选课数据
        initBloomFilter();

        new Thread(() -> {
            while (true) {
                try {
                    // 阻塞式弹出
                    Object payloadObj = redisTemplate.opsForList().rightPop(GLOBAL_QUEUE_KEY, 30, TimeUnit.SECONDS);
                    if (payloadObj != null) {
                        handleRush((String) payloadObj);
                        // 处理完一个后继续循环，立即尝试获取下一个
                        continue;
                    }
                } catch (Exception e) {
                    log.error("抢课消费者错误", e);
                    // 发生异常时适当休眠
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }).start();
    }

    private void handleRush(String payload) {
        // 负载格式: "courseId:studentNo"
        String[] parts = payload.split(":");
        if (parts.length != 2) return;
        String courseId = parts[0];
        String studentNo = parts[1];
        String statusKey = STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        String countKey = "course:rush:count:" + courseId;

        try {
            // 1. 获取课程容量和类型（在入队列时已带缓存，对性能影响较小）
            TeacherCourse teacherCourse = teacherCourseService.getTeacherCourse(courseId);
            
            // 2. 检查是否已选该课程 (使用布隆过滤器优化)
            String bloomKey = BLOOM_FILTER_KEY_PREFIX + courseId;
            if (isMember(bloomKey, studentNo)) {
                // 如果布隆过滤器判断可能已选，再查库确认（排除误判）
                boolean exists = studentCourseSelectionService.exists(new LambdaQueryWrapper<StudentCourseSelection>()
                        .eq(StudentCourseSelection::getCourseId, courseId)
                        .eq(StudentCourseSelection::getStudentNo, studentNo));
                if (exists) {
                    stringRedisTemplate.opsForValue().set(statusKey, "SUCCESS: 已选该课程", 5, TimeUnit.MINUTES);
                    return;
                }
            }

            // 3. 检查课程人数是否已满
            long selectedCount = studentCourseSelectionService.count(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getCourseId, courseId));
            if (selectedCount >= teacherCourse.getCapacity()) {
                stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 课程已满", 5, TimeUnit.MINUTES);
                return;
            }

            // 4. 检查选课时间冲突（通过 SQL 处理冲突检验逻辑）
            String conflictCourseId = studentCourseSelectionService.checkTimeConflict(studentNo, courseId);
            if (conflictCourseId != null) {
                stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 与已选课程时间冲突： " + conflictCourseId, 5, TimeUnit.MINUTES);
                return;
            }

            // 5. 确定课程类型（查询是否有班级覆盖）
            Integer finalCourseType = teacherCourse.getCourseType();
            Integer overrideType = studentCourseSelectionService.getOverrideCourseType(studentNo, courseId);
            if (overrideType != null) {
                finalCourseType = overrideType;
            }

            // 6. 抢课成功 - 保存选课记录
            StudentCourseSelection newSelection = new StudentCourseSelection();
            newSelection.setStudentNo(studentNo);
            newSelection.setCourseId(courseId);
            newSelection.setCourseCredit(teacherCourse.getCourseCredit()); 
            newSelection.setCourseType(finalCourseType);
            newSelection.setStudyStatus(0); // 0: 正常/在读状态
            studentCourseSelectionService.save(newSelection);
            
            // 抢课成功，更新布隆过滤器
            addToBloomFilter(bloomKey, studentNo);
            
            stringRedisTemplate.opsForValue().set(statusKey, "SUCCESS: 选课成功", 5, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("处理抢课请求时出错: " + payload, e);
            stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 系统错误", 5, TimeUnit.MINUTES);
        } finally {
            stringRedisTemplate.opsForValue().decrement(countKey);
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
}