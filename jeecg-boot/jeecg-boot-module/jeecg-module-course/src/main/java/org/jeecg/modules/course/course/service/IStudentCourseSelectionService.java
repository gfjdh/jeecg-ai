package org.jeecg.modules.course.course.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 学生选课表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
public interface IStudentCourseSelectionService extends IService<StudentCourseSelection> {

    /**
     * 统一抢课逻辑
     * @param courseId 课程ID
     * @param studentNo 学号
     * @return
     */
    Result<String> rush(String courseId, String studentNo);

    /**
     * 校验选课时间
     * @param studentNo 学号
     * @return 错误信息，若通过则返回 null
     */
    String validateSelectionTime(String studentNo);

    /**
     * 检查选课时间冲突
     * @param studentNo 学号
     * @param courseId 课程ID
     * @return 冲突的课程ID，如果没有冲突则返回 null
     */
    String checkTimeConflict(String studentNo, String courseId);

}
