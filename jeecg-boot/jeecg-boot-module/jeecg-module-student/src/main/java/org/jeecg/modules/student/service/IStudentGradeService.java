package org.jeecg.modules.student.service;

import org.jeecg.modules.student.entity.StudentGrade;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

/**
 * @Description: 学生成绩信息
 * @Author: qssh
 * @Date: 2026-01-27
 * @Version: V1.0
 */
public interface IStudentGradeService extends IService<StudentGrade> {

    /**
     * 分页查询
     * @param page
     * @param wrapper
     * @return
     */
    IPage<StudentGrade> queryStudentGradePage(Page<StudentGrade> page, Wrapper<StudentGrade> wrapper);

    /**
     * 自定义分页查询（包含业务逻辑）
     * @param studentGrade 实体对象
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param parameterMap 请求参数
     * @return 分页列表
     */
    IPage<StudentGrade> queryPageList(StudentGrade studentGrade, Integer pageNo, Integer pageSize, java.util.Map<String, String[]> parameterMap);
}
