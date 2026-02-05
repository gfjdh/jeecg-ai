package org.jeecg.modules.course.course.service.impl;

import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.mapper.ClassTimeMapper;
import org.jeecg.modules.course.course.mapper.TeacherCourseMapper;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @Description: 教师课程安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Service
public class TeacherCourseServiceImpl extends ServiceImpl<TeacherCourseMapper, TeacherCourse> implements ITeacherCourseService {

	@Autowired
	private TeacherCourseMapper teacherCourseMapper;
	@Autowired
	private ClassTimeMapper classTimeMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(TeacherCourse teacherCourse, List<ClassTime> classTimeList) {
		teacherCourseMapper.insert(teacherCourse);
		if(classTimeList!=null && classTimeList.size()>0) {
			for(ClassTime entity:classTimeList) {
				//外键设置
				entity.setCourseId(teacherCourse.getCourseId());
				classTimeMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(TeacherCourse teacherCourse,List<ClassTime> classTimeList) {
		teacherCourseMapper.updateById(teacherCourse);
		
		//1.先删除子表数据
		classTimeMapper.deleteByMainId(teacherCourse.getCourseId());
		
		//2.子表数据重新插入
		if(classTimeList!=null && classTimeList.size()>0) {
			for(ClassTime entity:classTimeList) {
				//外键设置
				entity.setCourseId(teacherCourse.getCourseId());
				classTimeMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		TeacherCourse teacherCourse = teacherCourseMapper.selectById(id);
		if(teacherCourse!=null) {
			classTimeMapper.deleteByMainId(teacherCourse.getCourseId());
		}
		teacherCourseMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			TeacherCourse teacherCourse = teacherCourseMapper.selectById(id);
			if(teacherCourse!=null) {
				classTimeMapper.deleteByMainId(teacherCourse.getCourseId());
			}
			teacherCourseMapper.deleteById(id);
		}
	}

    @Override
    public TeacherCourse getTeacherCourseCached(String courseId) {
        String key = "course:info:" + courseId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            if (cached instanceof TeacherCourse) {
                return (TeacherCourse) cached;
            }
            if (cached instanceof Map) {
                Map<?,?> map = (Map<?,?>) cached;
                TeacherCourse c = new TeacherCourse();
                c.setCourseId((String) map.get("courseId"));
                // 处理数值类型可能的差异 (Integer/Long)
                Object cap = map.get("capacity");
                if (cap instanceof Number) {
                    c.setCapacity(((Number) cap).intValue());
                }
                
                Object type = map.get("courseType");
                if (type instanceof Number) {
                    c.setCourseType(((Number) type).intValue());
                }
                
                Object credit = map.get("courseCredit");
                if (credit instanceof Number) {
                    c.setCourseCredit(((Number) credit).intValue());
                }
                return c;
            }
        }
        TeacherCourse course = this.getOne(new LambdaQueryWrapper<TeacherCourse>().eq(TeacherCourse::getCourseId, courseId));
        if (course != null) {
            // 缓存10分钟
            redisTemplate.opsForValue().set(key, course, 10, TimeUnit.MINUTES);
        }
        return course;
    }
	
}
