package org.jeecg.modules.course.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.StudentSchedule;
import org.jeecg.modules.course.course.vo.StudentCourseSummaryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 学生选课表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
public interface StudentCourseSelectionMapper extends BaseMapper<StudentCourseSelection> {

    /**
     * 查询学生课表
     * @param studentNo 学号
     * @return 课表列表
     */
    List<StudentSchedule> getStudentSchedule(@Param("studentNo") String studentNo);

    /**
     * 获取可选课程列表
     * @param studentNo 学号
     * @param subject 科目
     * @param courseType 课程类型
     * @return 课程列表
     */
    List<StudentSchedule> getAvailableCourses(@Param("studentNo") String studentNo,
                                              @Param("subject") String subject, 
                                              @Param("courseType") Integer courseType);

    /**
     * 获取学生选课学分统计
     * @param studentNo 学号
     * @return 统计结果
     */
    StudentCourseSummaryVO getCourseSummary(@Param("studentNo") String studentNo);

}
