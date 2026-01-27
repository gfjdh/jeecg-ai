package org.jeecg.modules.student.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import org.jeecg.modules.student.entity.Student;

import org.jeecg.modules.student.mapper.StudentMapper;
import org.jeecg.modules.student.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.desensitization.util.SensitiveInfoUtil;

/**
 * @Description: 学生信息服务实现类
 * 继承 ServiceImpl，泛型指定 Mapper 和 Entity
 * 自动注入 Mapper，实现 IService 接口中定义的方法
 * @Author: qssh
 * @Date: 2026-01-26
 * @Version: V1.0
 */
@Service
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private CacheManager cacheManager;


    @Override
    public IPage<Student> queryPageList(Student student, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap) {
        // 1. 生成查询条件
        QueryWrapper<Student> queryWrapper = QueryGenerator.initQueryWrapper(student, parameterMap);

        // 2. 构建分页对象
        Page<Student> page = new Page<Student>(pageNo, pageSize);

        // 3. 调用 Service 查询
        IPage<Student> pageList = this.page(page, queryWrapper);

        // 手动脱敏处理
        Cache cache = cacheManager.getCache("student:info#30");
        for (Student s : pageList.getRecords()) {
            try {
                // 将查询到的结果存入 redis 中，供 getById 使用
                if (cache != null) {
                    cache.put(s.getId(), s);
                }
                SensitiveInfoUtil.handlerObject(s, true);
            } catch (IllegalAccessException e) {
                log.error("脱敏处理失败", e);
            }
        }
        return pageList;
    }

    /**
     * @Cacheable自定义TTL（单位是秒，目前只支持这一种单位）
     */
    @Override
    @Cacheable(cacheNames = "student:info#30", key = "#id")
    public Student getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public void deleteBatch(String ids) {
        super.removeByIds(Arrays.asList(ids.split(",")));
    }

    @Override
    public void saveBatch(java.util.List<Student> studentList) {
        super.saveBatch(studentList);
    }
}
