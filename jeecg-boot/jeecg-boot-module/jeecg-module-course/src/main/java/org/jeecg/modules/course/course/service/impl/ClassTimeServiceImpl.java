package org.jeecg.modules.course.course.service.impl;

import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.mapper.ClassTimeMapper;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Description: 课程时间安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Service
public class ClassTimeServiceImpl extends ServiceImpl<ClassTimeMapper, ClassTime> implements IClassTimeService {
	
	@Autowired
	private ClassTimeMapper classTimeMapper;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ClassTime> selectByMainId(String mainId) {
		String key = "course:time:" + mainId;
		List<ClassTime> list = (List<ClassTime>) redisTemplate.opsForValue().get(key);
		if (list == null) {
			list = classTimeMapper.selectByMainId(mainId);
			if (list != null) {
				redisTemplate.opsForValue().set(key, list, 10, TimeUnit.MINUTES);
			}
		}
		return list;
	}
}
