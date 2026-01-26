package org.jeecg.modules.student.service;

import org.jeecg.modules.student.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Map;

/**
 * @Description: 学生信息服务接口
 * 继承 IService 以复用 MyBatis-Plus 提供的 Service 层封装方法
 * 如 save, removeById, updateById, getById, page 等
 * @Author: qssh
 * @Date: 2026-01-26
 * @Version: V1.0
 */
public interface IStudentService extends IService<Student> {

    /**
     * 分页查询
     * @param student
     * @param pageNo
     * @param pageSize
     * @param parameterMap
     * @return
     */
    IPage<Student> queryPageList(Student student, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(String ids);
}
