package org.jeecg.modules.student.service.impl;

import org.jeecg.modules.student.entity.StudentGrade;
import org.jeecg.modules.student.mapper.StudentGradeMapper;
import org.jeecg.modules.student.service.IStudentGradeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryGenerator;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 学生成绩信息
 * @Author: qssh
 * @Date: 2026-01-27
 * @Version: V1.0
 */
@Service
@Slf4j
public class StudentGradeServiceImpl extends ServiceImpl<StudentGradeMapper, StudentGrade> implements IStudentGradeService {

    @Override
    public IPage<StudentGrade> queryStudentGradePage(Page<StudentGrade> page, Wrapper<StudentGrade> wrapper) {
        return this.baseMapper.queryStudentGradePage(page, wrapper);
    }

    @Override
    public IPage<StudentGrade> queryPageList(StudentGrade studentGrade, Integer pageNo, Integer pageSize, java.util.Map<String, String[]> parameterMap) {
        QueryWrapper<StudentGrade> queryWrapper = QueryGenerator.initQueryWrapper(studentGrade, parameterMap);
        // 自定义查询条件: 年级和班级 (关联表字段)
        if(studentGrade.getYear() != null && !studentGrade.getYear().isEmpty()){
            // 去除前端传值的*号，避免与mybatis-plus的like方法重复
            queryWrapper.like("s.year", studentGrade.getYear().replace("*", ""));
        }
        if(studentGrade.getClassName() != null && !studentGrade.getClassName().isEmpty()){
            queryWrapper.like("s.class_name", studentGrade.getClassName().replace("*", ""));
        }
        log.info("查询条件: " + queryWrapper.getSqlSegment());
        Page<StudentGrade> page = new Page<StudentGrade>(pageNo, pageSize);
        return this.baseMapper.queryStudentGradePage(page, queryWrapper);
    }
}
