package org.jeecg.modules.course.course.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.course.course.component.CourseRushConsumer;
import org.jeecg.modules.course.course.entity.ClassCourseType;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.entity.StudentSchedule;
import org.jeecg.modules.course.course.mapper.StudentCourseSelectionMapper;
import org.jeecg.modules.course.course.service.IClassCourseTypeService;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.jeecg.modules.student.entity.Student;
import org.jeecg.modules.student.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ITeacherCourseService teacherCourseService;

    @Autowired
    private IClassTimeService classTimeService;
    
    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private IClassCourseTypeService classCourseTypeService;

    /**
     * 获取学生学分统计面板
     */
    @GetMapping(value = "/summary")
    public Result<Map<String, Object>> summary() {
        return Result.OK();
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
     * @param subject 课程名称或编码
     * @param courseType 筛选课程类型 (1: 必修, 2: 选修)
     */
    @GetMapping(value = "/available")
    public Result<IPage<StudentSchedule>> available(
            @RequestParam(name="subject", required=false) String subject,
            @RequestParam(name="courseType", required=false) Integer courseTypeQuery,
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 获取学生所属班级和年级信息
        Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, user.getUsername()));
        if (student == null) {
            return Result.error("未找到学生档案信息");
        }
        
        List<StudentSchedule> list = studentCourseSelectionMapper.getAvailableCourses(
            user.getUsername(), 
            student.getClassName(), 
            student.getYear(),
            subject, 
            courseTypeQuery
        );
        IPage<StudentSchedule> page = new Page<>(pageNo, pageSize);
        page.setRecords(list);
        page.setTotal(list.size());
        return Result.OK(page);
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

}
