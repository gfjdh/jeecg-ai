package org.jeecg.modules.course.course.mapper;

import java.util.List;
import org.jeecg.modules.course.course.entity.ClassTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 课程时间安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
public interface ClassTimeMapper extends BaseMapper<ClassTime> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<ClassTime>
   */
	public List<ClassTime> selectByMainId(@Param("mainId") String mainId);
}
