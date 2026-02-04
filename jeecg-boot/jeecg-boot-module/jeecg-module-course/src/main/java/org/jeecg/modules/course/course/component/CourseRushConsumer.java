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
                    // Blocking pop, wait 1 second
                    Object payloadObj = redisTemplate.opsForList().rightPop(GLOBAL_QUEUE_KEY, 1, TimeUnit.SECONDS);
                    if (payloadObj != null) {
                        handleRush((String) payloadObj);
                    }
                } catch (Exception e) {
                    log.error("Course rush consumer error", e);
                    try { Thread.sleep(1000); } catch (InterruptedException ie) {}
                }
            }
        }).start();
    }

    private void handleRush(String payload) {
        // payload format: "courseId:studentNo"
        String[] parts = payload.split(":");
        if (parts.length != 2) return;
        String courseId = parts[0];
        String studentNo = parts[1];
        String statusKey = STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        String countKey = "course:rush:count:" + courseId;

        try {
            // 1. Check if already selected
            long count = studentCourseSelectionService.count(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getStudentNo, studentNo)
                    .eq(StudentCourseSelection::getCourseId, courseId));
            if (count > 0) {
                redisTemplate.opsForValue().set(statusKey, "SUCCESS: Already selected");
                return;
            }

            // 2. Check Capacity (Double check DB)
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

            // 3. Check Time Conflict
            // Get new course times
            List<ClassTime> newCourseTimes = classTimeService.list(new LambdaQueryWrapper<ClassTime>()
                    .eq(ClassTime::getCourseId, courseId));
            
            // Get student's existing detailed schedule
             List<StudentCourseSelection> existingSelections = studentCourseSelectionService.list(new LambdaQueryWrapper<StudentCourseSelection>()
                    .eq(StudentCourseSelection::getStudentNo, studentNo));
            
             for (StudentCourseSelection scs : existingSelections) {
                 List<ClassTime> existingTimes = classTimeService.list(new LambdaQueryWrapper<ClassTime>()
                         .eq(ClassTime::getCourseId, scs.getCourseId()));
                 
                 for (ClassTime newTime : newCourseTimes) {
                     for (ClassTime existTime : existingTimes) {
                         // null check
                         if (newTime.getWeekday() == null || existTime.getWeekday() == null) {
                             continue;
                         }
                         if (newTime.getWeekday().equals(existTime.getWeekday())) {
                             // Check overlap
                             if (newTime.getStartSection() != null && newTime.getEndSection() != null &&
                                 existTime.getStartSection() != null && existTime.getEndSection() != null) {
                                 if (isOverlap(newTime.getStartSection(), newTime.getEndSection(), 
                                               existTime.getStartSection(), existTime.getEndSection())) {
                                     redisTemplate.opsForValue().set(statusKey, "FAILED: Time Conflict with " + scs.getCourseId());
                                     return;
                                 }
                             }
                         }
                     }
                 }
             }

            // 4. Determine Course Type (Check override)
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

            // 5. Success - Save
            StudentCourseSelection newSelection = new StudentCourseSelection();
            newSelection.setStudentNo(studentNo);
            newSelection.setCourseId(courseId);
            if (teacherCourse.getCourseCredit() != null) {
                newSelection.setCourseCredit(new BigDecimal(teacherCourse.getCourseCredit())); 
            }
            newSelection.setCourseType(finalCourseType);
            newSelection.setStudyStatus(0); // 0: Normal/In Progress
            studentCourseSelectionService.save(newSelection);
            
            redisTemplate.opsForValue().set(statusKey, "SUCCESS");

        } catch (Exception e) {
            log.error("Error processing rush for " + payload, e);
            redisTemplate.opsForValue().set(statusKey, "FAILED: System Error");
        } finally {
            redisTemplate.opsForValue().decrement(countKey);
        }
    }

    private boolean isOverlap(int start1, int end1, int start2, int end2) {
        return Math.max(start1, start2) <= Math.min(end1, end2);
    }
}
