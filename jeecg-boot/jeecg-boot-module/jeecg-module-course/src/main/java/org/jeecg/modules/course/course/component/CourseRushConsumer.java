package org.jeecg.modules.course.course.component;

import java.util.List;
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
import java.math.BigDecimal;

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

    @PostConstruct
    public void startConsumer() {
        new Thread(() -> {
            while (true) {
                try {
                    // 非阻塞弹出
                    Object payloadObj = redisTemplate.opsForList().rightPop(GLOBAL_QUEUE_KEY);
                    if (payloadObj != null) {
                        handleRush((String) payloadObj);
                        // 处理完一个后继续循环，立即尝试获取下一个
                        continue;
                    }
                    // 队列为空时，休眠100ms再检查
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("抢课消费者错误", e);
                    // 发生异常时也适当休眠，避免CPU空转
                    try {
                        Thread.sleep(100);
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
            // 1. 检查课程容量（在入队列时已带缓存检查，对性能影响较小）
            TeacherCourse teacherCourse = teacherCourseService.getTeacherCourseCached(courseId);
            if (teacherCourse == null) {
                stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 课程不存在", 5, TimeUnit.MINUTES);
                return;
            }
            
            // 2. 合并查询：一次性获取相关选课信息和上课时间
            // 获取：1.该课程的所有选课(查容量) 2.该学生的所有选课(查重复和冲突)
            List<StudentCourseSelection> allRelatedSelections = studentCourseSelectionService.list(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getCourseId, courseId)
                    .or()
                    .eq(StudentCourseSelection::getStudentNo, studentNo));

            // 过滤出该课程的当前选课人数
            long selectedCount = allRelatedSelections.stream()
                    .filter(s -> courseId.equals(s.getCourseId()))
                    .count();
            if (selectedCount >= teacherCourse.getCapacity()) {
                stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 课程已满", 5, TimeUnit.MINUTES);
                return;
            }

            // 过滤出该学生是否已选过该课程
            boolean alreadySelected = allRelatedSelections.stream()
                    .anyMatch(s -> courseId.equals(s.getCourseId()) && studentNo.equals(s.getStudentNo()));
            if (alreadySelected) {
                stringRedisTemplate.opsForValue().set(statusKey, "SUCCESS: 已选该课程", 5, TimeUnit.MINUTES);
                return;
            }

            // 3. 检查选课时间冲突（通过 SQL 进行联表查询）
            String conflictCourseId = studentCourseSelectionService.checkTimeConflict(studentNo, courseId);
            if (conflictCourseId != null) {
                stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 与已选课程时间冲突： " + conflictCourseId, 5, TimeUnit.MINUTES);
                return;
            }

            // 4. 确定课程类型（查询是否有班级覆盖）
            Integer finalCourseType = teacherCourse.getCourseType();
            Integer overrideType = studentCourseSelectionService.getOverrideCourseType(studentNo, courseId);
            if (overrideType != null) {
                finalCourseType = overrideType;
            }

            // 5. 抢课成功 - 保存选课记录
            StudentCourseSelection newSelection = new StudentCourseSelection();
            newSelection.setStudentNo(studentNo);
            newSelection.setCourseId(courseId);
            if (teacherCourse.getCourseCredit() != null) {
                newSelection.setCourseCredit(new BigDecimal(teacherCourse.getCourseCredit())); 
            }
            newSelection.setCourseType(finalCourseType);
            newSelection.setStudyStatus(0); // 0: 正常/在读状态
            studentCourseSelectionService.save(newSelection);
            
            stringRedisTemplate.opsForValue().set(statusKey, "选课成功", 5, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("处理抢课请求时出错: " + payload, e);
            stringRedisTemplate.opsForValue().set(statusKey, "FAILED: 系统错误", 5, TimeUnit.MINUTES);
        } finally {
            stringRedisTemplate.opsForValue().decrement(countKey);
        }
    }
}