package org.jeecg.modules.student.mapper;

import org.jeecg.modules.student.entity.StudentGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;

/**
 * @Description: 学生成绩信息
 * @Author: qssh
 * @Date: 2026-01-27
 * @Version: V1.0
 */
@Mapper
public interface StudentGradeMapper extends BaseMapper<StudentGrade> {

    /**
     * 分页查询学生成绩（包含学生信息）
     * @param page
     * @param wrapper
     * @return
     */
    IPage<StudentGrade> queryStudentGradePage(Page<StudentGrade> page, @Param(Constants.WRAPPER) Wrapper<StudentGrade> wrapper);

}
