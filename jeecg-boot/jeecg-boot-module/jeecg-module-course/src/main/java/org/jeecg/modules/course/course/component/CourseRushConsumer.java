package org.jeecg.modules.course.course.component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;
import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.course.course.entity.ClassCourseType;
import org.jeecg.modules.course.course.service.IClassCourseTypeService;
import org.jeecg.modules.student.entity.Student;
import org.jeecg.modules.student.service.IStudentService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.math.BigDecimal;

@Slf4j
@Component
public class CourseRushConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private IStudentCourseSelectionService studentCourseSelectionService;
    
    @Autowired
    private ITeacherCourseService teacherCourseService;
    
    @Autowired
    private IClassTimeService classTimeService;
    
    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private IClassCourseTypeService classCourseTypeService;

    public static final String GLOBAL_QUEUE_KEY = "course:rush:global_queue";
    public static final String STATUS_KEY_PREFIX = "course:rush:status:";

    @PostConstruct
    public void startConsumer() {
        new Thread(() -> {
            while (true) {
                try {
                    // 阻塞式弹出，等待10ms后继续循环
                    Object payloadObj = redisTemplate.opsForList().rightPop(GLOBAL_QUEUE_KEY, 10, TimeUnit.MILLISECONDS);
                    if (payloadObj != null) {
                        handleRush((String) payloadObj);
                    }
                } catch (Exception e) {
                    log.error("课程抢购消费者错误", e);
                    try { Thread.sleep(1000); } catch (InterruptedException ie) {}
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
            // 1. 检查是否已选过该课程
            long count = studentCourseSelectionService.count(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getStudentNo, studentNo)
                    .eq(StudentCourseSelection::getCourseId, courseId));
            if (count > 0) {
                redisTemplate.opsForValue().set(statusKey, "SUCCESS: Already selected");
                return;
            }

            // 2. 检查课程容量（数据库双重检查）
            TeacherCourse teacherCourse = teacherCourseService.getOne(new LambdaQueryWrapper<TeacherCourse>()
                    .eq(TeacherCourse::getCourseId, courseId));
            if (teacherCourse == null) {
                redisTemplate.opsForValue().set(statusKey, "FAILED: Course not found");
                return;
            }
            
            long selectedCount = studentCourseSelectionService.count(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getCourseId, courseId));
            if (selectedCount >= teacherCourse.getCapacity()) {
                redisTemplate.opsForValue().set(statusKey, "FAILED: Course Full");
                return;
            }

            // 3. 检查时间冲突
            // 获取新课程的上课时间
            List<ClassTime> newCourseTimes = classTimeService.list(new LambdaQueryWrapper<ClassTime>()
                    .eq(ClassTime::getCourseId, courseId));
            
            // 获取学生现有的详细课程安排
             List<StudentCourseSelection> existingSelections = studentCourseSelectionService.list(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getStudentNo, studentNo));
            
             for (StudentCourseSelection scs : existingSelections) {
                 List<ClassTime> existingTimes = classTimeService.list(new LambdaQueryWrapper<ClassTime>()
                         .eq(ClassTime::getCourseId, scs.getCourseId()));
                 
                 for (ClassTime newTime : newCourseTimes) {
                     for (ClassTime existTime : existingTimes) {
                         // 空值检查
                         if (newTime.getWeekday() == null || existTime.getWeekday() == null) {
                             continue;
                         }
                         if (newTime.getWeekday().equals(existTime.getWeekday())) {
                             // 检查时间段是否重叠
                             if (newTime.getStartSection() != null && newTime.getEndSection() != null &&
                                 existTime.getStartSection() != null && existTime.getEndSection() != null) {
                                 if (isOverlap(newTime.getStartSection(), newTime.getEndSection(), 
                                               existTime.getStartSection(), existTime.getEndSection())) {
                                     redisTemplate.opsForValue().set(statusKey, "FAILED: 与已选课程时间冲突： " + scs.getCourseId());
                                     return;
                                 }
                             }
                         }
                     }
                 }
             }

            // 4. 确定课程类型（检查是否有班级覆盖）
            Integer finalCourseType = teacherCourse.getCourseType();
            Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, studentNo));
            if (student != null) {
                ClassCourseType cct = classCourseTypeService.getOne(new LambdaQueryWrapper<ClassCourseType>()
                        .eq(ClassCourseType::getClassId, student.getClassName())
                        .eq(ClassCourseType::getYear, student.getYear())
                        .eq(ClassCourseType::getCourseId, courseId));
                if (cct != null && cct.getCourseType() != null) {
                    finalCourseType = cct.getCourseType();
                }
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
            
            redisTemplate.opsForValue().set(statusKey, "SUCCESS");

        } catch (Exception e) {
            log.error("处理抢课请求时出错: " + payload, e);
            redisTemplate.opsForValue().set(statusKey, "FAILED: System Error");
        } finally {
            redisTemplate.opsForValue().decrement(countKey);
        }
    }

    /**
     * 检查两个时间段是否重叠
     * @param start1 时间段1的开始节次
     * @param end1 时间段1的结束节次
     * @param start2 时间段2的开始节次
     * @param end2 时间段2的结束节次
     * @return 如果重叠返回true，否则返回false
     */
    private boolean isOverlap(int start1, int end1, int start2, int end2) {
        return Math.max(start1, start2) <= Math.min(end1, end2);
    }
}