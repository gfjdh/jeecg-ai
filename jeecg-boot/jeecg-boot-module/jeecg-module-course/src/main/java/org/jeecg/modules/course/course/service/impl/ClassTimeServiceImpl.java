package org.jeecg.modules.course.course.service.impl;

import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.mapper.ClassTimeMapper;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

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
	
	@Override
	public List<ClassTime> selectByMainId(String mainId) {
		return classTimeMapper.selectByMainId(mainId);
	}
}
