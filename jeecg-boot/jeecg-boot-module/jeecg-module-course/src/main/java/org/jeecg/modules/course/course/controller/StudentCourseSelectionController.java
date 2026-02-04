package org.jeecg.modules.course.course.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.component.CourseRushConsumer;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.springframework.data.redis.core.RedisTemplate;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 学生选课表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Tag(name="学生选课表")
@RestController
@RequestMapping("/course/studentCourseSelection")
@Slf4j
public class StudentCourseSelectionController extends JeecgController<StudentCourseSelection, IStudentCourseSelectionService> {
	@Autowired
	private IStudentCourseSelectionService studentCourseSelectionService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ITeacherCourseService teacherCourseService;

    @Autowired
    private IClassTimeService classTimeService;
    
    // --- Course Selection / Rush Logic ---

    /**
     * Get Student Credit Dashboard
     */
    @GetMapping(value = "/summary")
    public Result<Map<String, Object>> summary() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        
        List<StudentCourseSelection> list = studentCourseSelectionService.list(new LambdaQueryWrapper<StudentCourseSelection>()
             .eq(StudentCourseSelection::getStudentNo, studentNo));
        
        BigDecimal selectedCompulsory = BigDecimal.ZERO;
        BigDecimal selectedElective = BigDecimal.ZERO;
        BigDecimal completedCompulsory = BigDecimal.ZERO;
        BigDecimal completedElective = BigDecimal.ZERO;
        
        // Assumption: courseType 1 = Compulsory, 2 = Elective (Adjust as per Dict)
        // Assumption: studyStatus 1 = Completed, 0 = Selected (Adjust as per Dict)
        
        for (StudentCourseSelection s : list) {
            BigDecimal credit = s.getCourseCredit() != null ? s.getCourseCredit() : BigDecimal.ZERO;
            boolean isCompleted = Integer.valueOf(1).equals(s.getStudyStatus()); // 1 is just guess, usually based on dict
            boolean isCompulsory = Integer.valueOf(1).equals(s.getCourseType()); // 1 is just guess
            
            if (isCompulsory) {
                if (isCompleted) completedCompulsory = completedCompulsory.add(credit);
                else selectedCompulsory = selectedCompulsory.add(credit);
            } else {
                if (isCompleted) completedElective = completedElective.add(credit);
                else selectedElective = selectedElective.add(credit);
            }
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("selectedCompulsory", selectedCompulsory);
        map.put("selectedElective", selectedElective);
        map.put("completedCompulsory", completedCompulsory);
        map.put("completedElective", completedElective);
        return Result.OK(map);
    }

    /**
     * Get Student Schedule
     */
    @GetMapping(value = "/schedule")
    public Result<List<Map<String, Object>>> schedule() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        
        // 1. Get selected courses
        List<StudentCourseSelection> selections = studentCourseSelectionService.list(new LambdaQueryWrapper<StudentCourseSelection>()
             .eq(StudentCourseSelection::getStudentNo, studentNo));
        
        if (selections.isEmpty()) {
            return Result.OK(java.util.Collections.emptyList());
        }
        
        List<String> courseIds = selections.stream().map(StudentCourseSelection::getCourseId).collect(Collectors.toList());
        
        // 2. Get Class Times
        List<ClassTime> times = classTimeService.list(new LambdaQueryWrapper<ClassTime>()
             .in(ClassTime::getCourseId, courseIds));
             
        // 3. Get Teacher/Course Details
        List<TeacherCourse> courses = teacherCourseService.list(new LambdaQueryWrapper<TeacherCourse>()
             .in(TeacherCourse::getCourseId, courseIds));
        Map<String, TeacherCourse> courseMap = courses.stream().collect(Collectors.toMap(TeacherCourse::getCourseId, c->c));
        
        // 4. Merge
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (ClassTime t : times) {
             TeacherCourse c = courseMap.get(t.getCourseId());
             if (c != null) {
                 Map<String, Object> item = new HashMap<>();
                 item.put("courseId", c.getCourseId());
                 item.put("courseName", c.getCourse()); // 'course' field is usually name or code. Assuming name/subject
                 item.put("teacherNo", c.getTeacherNo());
                 item.put("weekday", t.getWeekday());
                 item.put("startSection", t.getStartSection());
                 item.put("endSection", t.getEndSection());
                 item.put("location", t.getLocation());
                 result.add(item);
             }
        }
        return Result.OK(result);
    }
    
    /**
     * Get Available Courses
     */
    @GetMapping(value = "/available")
    public Result<List<TeacherCourse>> available(@RequestParam(name="subject", required=false) String subject) {
        // Return all TeacherCourse (optionally filtered by subject)
        // In real app, filter out already selected? Frontend can handle that or here.
        LambdaQueryWrapper<TeacherCourse> query = new LambdaQueryWrapper<>();
        if (subject != null && !subject.isEmpty()) {
            query.like(TeacherCourse::getCourse, subject);
        }
        List<TeacherCourse> list = teacherCourseService.list(query);
        return Result.OK(list);
    }

    /**
     * Rush Course
     */
    @PostMapping(value = "/rush")
    public Result<String> rush(@RequestBody Map<String, String> json) {
        String courseId = json.get("courseId");
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        
        if (courseId == null) return Result.error("Course ID required");
        
        // 1. Check Queue Limit
        // Redundancy 1.25 -> 50 for 40
        TeacherCourse course = teacherCourseService.getOne(new LambdaQueryWrapper<TeacherCourse>().eq(TeacherCourse::getCourseId, courseId));
        if (course == null) return Result.error("Course not found");
        
        long limit = (long) (course.getCapacity() * 1.25); // Hardcoded redundancy
        String countKey = "course:rush:count:" + courseId;
        
        long currentCount = redisTemplate.opsForValue().increment(countKey);
        if (currentCount > limit) {
        	redisTemplate.opsForValue().decrement(countKey);
            return Result.error("Course Full (Queue Full)");
        }
        
        // 2. Push to Queue
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        redisTemplate.opsForValue().set(statusKey, "PENDING");
        
        String payload = courseId + ":" + studentNo;
        redisTemplate.opsForList().leftPush(CourseRushConsumer.GLOBAL_QUEUE_KEY, payload);
        
        return Result.OK("Queued. Please wait...");
    }
    
    /**
     * Check Rush Status
     */
    @GetMapping(value = "/rush/status")
    public Result<String> rushStatus(@RequestParam(name="courseId") String courseId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        
        Object status = redisTemplate.opsForValue().get(statusKey);
        if (status == null) return Result.error("No record");
        return Result.OK(status.toString());
    }
    
    // --- End Course Selection Logic ---

	/**
	 * 分页列表查询
	 *
	 * @param studentCourseSelection
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "学生选课表-分页列表查询")
	@Operation(summary="学生选课表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StudentCourseSelection>> queryPageList(StudentCourseSelection studentCourseSelection,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {


        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("courseType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("studyStatus", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<StudentCourseSelection> queryWrapper = QueryGenerator.initQueryWrapper(studentCourseSelection, req.getParameterMap(),customeRuleMap);
		Page<StudentCourseSelection> page = new Page<StudentCourseSelection>(pageNo, pageSize);
		IPage<StudentCourseSelection> pageList = studentCourseSelectionService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param studentCourseSelection
	 * @return
	 */
	@AutoLog(value = "学生选课表-添加")
	@Operation(summary="学生选课表-添加")
	@RequiresPermissions("course:student_course_selection:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody StudentCourseSelection studentCourseSelection) {
		studentCourseSelectionService.save(studentCourseSelection);

		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param studentCourseSelection
	 * @return
	 */
	@AutoLog(value = "学生选课表-编辑")
	@Operation(summary="学生选课表-编辑")
	@RequiresPermissions("course:student_course_selection:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StudentCourseSelection studentCourseSelection) {
		studentCourseSelectionService.updateById(studentCourseSelection);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "学生选课表-通过id删除")
	@Operation(summary="学生选课表-通过id删除")
	@RequiresPermissions("course:student_course_selection:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		studentCourseSelectionService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "学生选课表-批量删除")
	@Operation(summary="学生选课表-批量删除")
	@RequiresPermissions("course:student_course_selection:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.studentCourseSelectionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "学生选课表-通过id查询")
	@Operation(summary="学生选课表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StudentCourseSelection> queryById(@RequestParam(name="id",required=true) String id) {
		StudentCourseSelection studentCourseSelection = studentCourseSelectionService.getById(id);
		if(studentCourseSelection==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(studentCourseSelection);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param studentCourseSelection
    */
    @RequiresPermissions("course:student_course_selection:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StudentCourseSelection studentCourseSelection) {
        return super.exportXls(request, studentCourseSelection, StudentCourseSelection.class, "学生选课表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("course:student_course_selection:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, StudentCourseSelection.class);
    }

}
