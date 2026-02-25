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
     * 保存选课记录 (事务操作)
     * @param studentCourseSelection 选课信息
     */
    void saveSelectionWithTransaction(StudentCourseSelection studentCourseSelection);

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

    /**
     * 获取学生针对特定课程的覆盖课程类型
     * @param studentNo 学号
     * @param courseId 课程ID
     * @return 覆盖课程类型
     */
    Integer getOverrideCourseType(String studentNo, String courseId);

    /**
     * 同步操作缓存：如果缓存存在，将新课程的时间加入缓存
     * @param studentNo 学号
     * @param courseId 课程ID
     */
    void addStudentTimeCache(String studentNo, String courseId);

    /**
     * 删除学生课程时间缓存
     * @param studentNo 学号
     */
    void deleteStudentTimeCache(String studentNo);
}
