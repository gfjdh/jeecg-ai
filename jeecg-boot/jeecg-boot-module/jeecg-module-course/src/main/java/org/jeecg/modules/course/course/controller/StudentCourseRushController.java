package org.jeecg.modules.course.course.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.course.course.component.CourseRushConsumer;
import org.jeecg.modules.course.course.vo.StudentCourseSummaryVO;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.StudentSchedule;
import org.jeecg.modules.course.course.mapper.StudentCourseSelectionMapper;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 学生选课操作 (选课/抢课逻辑)
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Tag(name="学生选课操作")
@RestController
@RequestMapping("/course/studentCourseRush")
@Slf4j
public class StudentCourseRushController {

    @Autowired
    private IStudentCourseSelectionService studentCourseSelectionService;

    @Autowired
    private StudentCourseSelectionMapper studentCourseSelectionMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取学生学分统计面板
     */
    @GetMapping(value = "/summary")
    public Result<IPage<StudentCourseSummaryVO>> summary() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        StudentCourseSummaryVO vo = studentCourseSelectionMapper.getCourseSummary(user.getUsername());
        if (vo == null) {
            vo = new StudentCourseSummaryVO();
        }
        IPage<StudentCourseSummaryVO> page = new Page<>();
        page.setRecords(java.util.Arrays.asList(vo));
        page.setTotal(1);
        return Result.OK(page);
    }

    /**
     * 获取学生课表
     */
    @GetMapping(value = "/schedule")
    public Result<IPage<StudentSchedule>> schedule() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<StudentSchedule> list = studentCourseSelectionMapper.getStudentSchedule(user.getUsername());
        IPage<StudentSchedule> page = new Page<>();
        page.setRecords(list);
        page.setTotal(list.size());
        return Result.OK(page);
    }
    
    /**
     * 获取可选课程列表
     * 根据班级动态调整课程属性 (必修/选修)
     * @param subject 课程名称 or 编码
     * @param courseTypeQuery 筛选课程类型 (1: 必修, 2: 选修)
     */
    @GetMapping(value = "/available")
    public Result<IPage<StudentSchedule>> available(
            @RequestParam(name="subject", required=false) String subject,
            @RequestParam(name="courseType", required=false) Integer courseTypeQuery,
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 验证选课时间
        String timeError = studentCourseSelectionService.validateSelectionTime(user.getUsername());
        if (timeError != null) {
            // 不在选课时间内，返回空列表
            return Result.OK(new Page<>());
        }
        
        List<StudentSchedule> list = studentCourseSelectionMapper.getAvailableCourses(
            user.getUsername(), 
            subject, 
            courseTypeQuery
        );
        IPage<StudentSchedule> page = new Page<>(pageNo, pageSize);
        page.setRecords(list);
        page.setTotal(list.size());
        return Result.OK(page);
    }

    /**
     * 抢课接口 (带 Auth)
     * 用于生产环境，前端调用时需携带 token
     */
    @PostMapping(value = "/rush")
    public Result<String> rushWithAuth(@RequestBody Map<String, String> json) {
        String courseId = json.get("courseId");
        if (courseId == null) return Result.error("缺少课程ID");

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 验证选课时间
        String timeError = studentCourseSelectionService.validateSelectionTime(user.getUsername());
        if (timeError != null) {
            // 不在选课时间内，返回错误信息
            return Result.error("不在选课时间内，无法选课");
        }
        String studentNo = user.getUsername();
                
        return studentCourseSelectionService.rush(courseId, studentNo);
    }

    /**
     * 抢课接口 (不带 Auth，用于压测)
     * @param json 需包含 courseId 和 studentNo
     */
    @IgnoreAuth
    @PostMapping(value = "/rushNoAuth")
    @Operation(summary = "抢课压测接口 (免授权)")
    public Result<String> rushNoAuth(@RequestBody Map<String, String> json) {
        String courseId = json.get("courseId");
        String studentNo = json.get("studentNo");
        if (courseId == null) return Result.error("缺少课程ID");
        if (studentNo == null) return Result.error("缺少学号");

        return studentCourseSelectionService.rush(courseId, studentNo);
    }
    
    /**
     * 抢课状态接口 (不带 Auth，用于压测)
     * @param json 需包含 courseId 和 studentNo
     */
    @IgnoreAuth
    @PostMapping(value = "/rushStatusNoAuth")
    @Operation(summary = "查询抢课状态压测接口 (免授权)")
    public Result<String> rushStatusNoAuth(@RequestBody Map<String, String> json) {
        String courseId = json.get("courseId");
        String studentNo = json.get("studentNo");
        if (courseId == null) return Result.error("缺少课程ID");
        if (studentNo == null) return Result.error("缺少学号");
        
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        
        Object status = stringRedisTemplate.opsForValue().get(statusKey);
        if (status == null) return Result.error("无排队记录");
        if ("PENDING".equals(status.toString())) return Result.OK("排队中");
        if (status.toString().startsWith("FAILED:")) {
            return Result.error(status.toString().substring(7)); 
        }
        return Result.OK(status.toString());
    }
    
    /**
     * 查询抢课状态
     */
    @GetMapping(value = "/rush/status")
    @Operation(summary = "查询抢课状态")
    public Result<String> rushStatus(@RequestParam(name="courseId") String courseId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        
        Object status = stringRedisTemplate.opsForValue().get(statusKey);
        if (status == null) return Result.error("无排队记录");
        if ("PENDING".equals(status.toString())) return Result.OK("");
        if (status.toString().startsWith("FAILED:")) {
            return Result.error(status.toString().substring(7)); 
        }
        return Result.OK(status.toString());
    }

    /**
     * 退课接口
     */
    @PostMapping(value = "/drop")
    public Result<String> drop(@RequestBody Map<String, String> json) {

        String courseId = json.get("courseId");
        if (courseId == null) return Result.error("缺少课程ID");
        
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 验证选课时间
        String timeError = studentCourseSelectionService.validateSelectionTime(user.getUsername());
        if (timeError != null) {
            // 不在选课时间内，返回错误信息
            return Result.error("不在选课时间内，无法退课");
        }
        studentCourseSelectionService.remove(new LambdaQueryWrapper<StudentCourseSelection>()
                .eq(StudentCourseSelection::getStudentNo, user.getUsername())
                .eq(StudentCourseSelection::getCourseId, courseId));
        return Result.OK("退课成功");
    }

}