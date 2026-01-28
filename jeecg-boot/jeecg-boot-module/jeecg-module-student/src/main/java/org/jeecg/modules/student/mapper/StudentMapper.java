package org.jeecg.modules.student.mapper;

import org.jeecg.modules.student.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 学生信息 Mapper 接口
 * 继承 BaseMapper 后，自动拥有了基本的 CRUD 方法
 * 无需编写 XML 文件即可使用 insert, delete, update, selectById 等方法
 * @Author: qssh
 * @Date: 2026-01-23
 * @Version: V1.0
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 如果需要自定义复杂的 SQL 查询，可以在这里定义方法，并在 XML 中实现
}
