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

import org.jeecg.modules.course.course.entity.ClassCourseType;
import org.jeecg.modules.course.course.service.IClassCourseTypeService;
import org.jeecg.modules.student.entity.Student;
import org.jeecg.modules.student.service.IStudentService;
import org.jeecg.modules.course.course.mapper.StudentCourseSelectionMapper;
import org.jeecg.modules.course.course.vo.StudentScheduleVo;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.BeanUtils;

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
    
    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private IClassCourseTypeService classCourseTypeService;
    
    // --- Course Selection / Rush Logic ---

    /**
     * 获取学生学分统计面板
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
        
        // 假设: courseType 1 = 必修, 2 = 选修 (请根据实际字典调整)
        // 假设: studyStatus 1 = 已修读, 0 = 选课中 (请根据实际字典调整)
        
        for (StudentCourseSelection s : list) {
            BigDecimal credit = s.getCourseCredit() != null ? s.getCourseCredit() : BigDecimal.ZERO;
            boolean isCompleted = Integer.valueOf(1).equals(s.getStudyStatus()); 
            boolean isCompulsory = Integer.valueOf(1).equals(s.getCourseType()); 
            
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
     * 获取学生课表
     */
    @GetMapping(value = "/schedule")
    public Result<List<StudentScheduleVo>> schedule() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        
        List<StudentScheduleVo> scheduleList = studentCourseSelectionMapper.getStudentSchedule(studentNo);
        return Result.OK(scheduleList);
    }
    
    /**
     * 获取可选课程列表
     * 支持根据班级动态调整课程属性 (必修/选修)
     * @param subject 课程名称或编码
     * @param courseType 筛选课程类型 (1: 必修, 2: 选修)
     */
    @GetMapping(value = "/available")
    public Result<List<TeacherCourse>> available(
            @RequestParam(name="subject", required=false) String subject,
            @RequestParam(name="courseType", required=false) Integer courseTypeQuery) {
        
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, user.getUsername()));
        
        LambdaQueryWrapper<TeacherCourse> query = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(subject)) {
            query.eq(TeacherCourse::getCourse, subject);
        }
        List<TeacherCourse> courses = teacherCourseService.list(query);
        
        Map<String, Integer> specialTypes = new HashMap<>();
        if (student != null) {
            List<ClassCourseType> ccts = classCourseTypeService.list(new LambdaQueryWrapper<ClassCourseType>()
                    .eq(ClassCourseType::getClassId, student.getClassName())
                    .eq(ClassCourseType::getYear, student.getYear()));
            for (ClassCourseType cct : ccts) {
                specialTypes.put(cct.getCourseId(), cct.getCourseType());
            }
        }
        
        List<TeacherCourse> result = new java.util.ArrayList<>();
        
        for (TeacherCourse tc : courses) {
            Integer effectiveType = specialTypes.containsKey(tc.getCourseId()) 
                    ? specialTypes.get(tc.getCourseId()) 
                    : tc.getCourseType();
            
            if (courseTypeQuery != null && !courseTypeQuery.equals(effectiveType)) {
                continue;
            }
            
            tc.setCourseType(effectiveType);
            result.add(tc);
        }
        
        return Result.OK(result);
    }

    /**
     * 抢课接口 (Rush Course)
     * 使用 Redis 队列处理高并发
     */
    @PostMapping(value = "/rush")
    public Result<String> rush(@RequestBody Map<String, String> json) {
        String courseId = json.get("courseId");
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        
        if (courseId == null) return Result.error("缺少课程ID");
        
        // 1. 检查队列限制 (Check Queue Limit)
        // 冗余设计: 允许 1.25 倍容量进入队列，防止因并发导致的超卖或误判
        TeacherCourse course = teacherCourseService.getOne(new LambdaQueryWrapper<TeacherCourse>().eq(TeacherCourse::getCourseId, courseId));
        if (course == null) return Result.error("课程不存在");
        
        long limit = (long) (course.getCapacity() * 1.25); 
        String countKey = "course:rush:count:" + courseId;
        
        long currentCount = redisTemplate.opsForValue().increment(countKey);
        if (currentCount > limit) {
        	redisTemplate.opsForValue().decrement(countKey);
            return Result.error("课程已满 (排队人数过多)");
        }
        
        // 2. 推入队列 (Push to Queue)
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        // 设置初始状态
        redisTemplate.opsForValue().set(statusKey, "PENDING");
        
        String payload = courseId + ":" + studentNo;
        // 发送到全局处理队列
        redisTemplate.opsForList().leftPush(CourseRushConsumer.GLOBAL_QUEUE_KEY, payload);
        
        return Result.OK("排队中，请稍候...");
    }
    
    /**
     * 查询抢课状态
     */
    @GetMapping(value = "/rush/status")
    public Result<String> rushStatus(@RequestParam(name="courseId") String courseId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        
        Object status = redisTemplate.opsForValue().get(statusKey);
        if (status == null) return Result.error("无排队记录");
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
