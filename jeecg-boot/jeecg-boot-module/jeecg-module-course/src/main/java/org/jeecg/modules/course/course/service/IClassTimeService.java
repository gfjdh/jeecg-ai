package org.jeecg.modules.course.course.service;

import org.jeecg.modules.course.course.entity.ClassTime;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 课程时间安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
public interface IClassTimeService extends IService<ClassTime> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<ClassTime>
	 */
	public List<ClassTime> selectByMainId(String mainId);
}
