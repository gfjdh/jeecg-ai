package org.jeecg.modules.course.course.service;

import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 教师课程安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
public interface ITeacherCourseService extends IService<TeacherCourse> {

	/**
	 * 添加一对多
	 *
	 * @param teacherCourse
	 * @param classTimeList
	 */
	public void saveMain(TeacherCourse teacherCourse,List<ClassTime> classTimeList) ;
	
	/**
	 * 修改一对多
	 *
   * @param teacherCourse
   * @param classTimeList
	 */
	public void updateMain(TeacherCourse teacherCourse,List<ClassTime> classTimeList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 获取课程信息（带缓存）
	 * @param courseId
	 * @return
	 */
	TeacherCourse getTeacherCourseCached(String courseId);
}