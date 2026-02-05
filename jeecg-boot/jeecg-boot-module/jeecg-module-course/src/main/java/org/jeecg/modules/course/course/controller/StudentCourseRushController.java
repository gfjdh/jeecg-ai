package org.jeecg.modules.course.course.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.course.course.component.CourseRushConsumer;
import org.jeecg.modules.course.course.vo.StudentCourseSummaryVO;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.entity.StudentSchedule;
import org.jeecg.modules.course.course.mapper.StudentCourseSelectionMapper;
import org.jeecg.modules.course.course.service.IClassCourseTypeService;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.jeecg.modules.course.course.entity.TrainingProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

    private static final RedisScript<Long> RUSH_SCRIPT;

    static {
        String lua = "local current = redis.call('get', KEYS[1]) or 0 " +
                     "if tonumber(current) >= tonumber(ARGV[1]) then " +
                     "   return -1 " +
                     "else " +
                     "   return redis.call('incr', KEYS[1]) " +
                     "end";
        RUSH_SCRIPT = new DefaultRedisScript<>(lua, Long.class);
    }

    @Autowired
    private IStudentCourseSelectionService studentCourseSelectionService;

    @Autowired
    private StudentCourseSelectionMapper studentCourseSelectionMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ITeacherCourseService teacherCourseService;

    @Autowired
    private IClassTimeService classTimeService;
    
    @Autowired
    private IClassCourseTypeService classCourseTypeService;

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

        // 验证选课时间
        String timeError = validateSelectionTime(user.getUsername());
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
     * 抢课接口 (Rush Course)
     * 使用 Redis 队列处理高并发
     */
    // @IgnoreAuth
    @PostMapping(value = "/rush")
    public Result<String> rush(@RequestBody Map<String, String> json) {
        String courseId = json.get("courseId");
        if (courseId == null) return Result.error("缺少课程ID");

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
                
        // 验证选课时间
        String timeError = validateSelectionTime(studentNo);
        if (timeError != null) {
            return Result.error(timeError);
        }

        // 1. 检查队列限制
        TeacherCourse course = teacherCourseService.getTeacherCourseCached(courseId);
        if (course == null) return Result.error("课程不存在");
        
        // 冗余设计: 允许 1.25 倍容量进入队列
        long limit = (long) (course.getCapacity() * 1.25); 
        String countKey = "course:rush:count:" + courseId;
        
        // 检查队列是否已满，使用lua脚本确保操作原子性
        Long result = stringRedisTemplate.execute(RUSH_SCRIPT, Collections.singletonList(countKey), String.valueOf(limit));
        if (result != null && result == -1) {
            return Result.error("课程已满 (排队人数过多)");
        }
        
        // 2. 推入队列
        String payload = courseId + ":" + studentNo;
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + payload;
        stringRedisTemplate.opsForValue().set(statusKey, "PENDING");        // 设置初始状态
        
        // 发送到全局处理队列
        redisTemplate.opsForList().leftPush(CourseRushConsumer.GLOBAL_QUEUE_KEY, payload);
        
        return Result.OK("进入排队");
    }
    
    /**
     * 查询抢课状态
     */
    @GetMapping(value = "/rush/status")
    public Result<String> rushStatus(@RequestParam(name="courseId") String courseId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String studentNo = user.getUsername();
        String statusKey = CourseRushConsumer.STATUS_KEY_PREFIX + courseId + ":" + studentNo;
        
        Object status = stringRedisTemplate.opsForValue().get(statusKey);
        if (status == null) return Result.error("无排队记录");
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
        studentCourseSelectionService.remove(new LambdaQueryWrapper<StudentCourseSelection>()
                .eq(StudentCourseSelection::getStudentNo, user.getUsername())
                .eq(StudentCourseSelection::getCourseId, courseId));
        return Result.OK("退课成功");
    }

    /**
     * 校验选课时间+缓存
     */
    @SuppressWarnings("unchecked")
    private String validateSelectionTime(String studentNo) {
        String key = "course:rush:time:" + studentNo;
        // 从缓存获取时间配置
        Map<String, Object> timeConfig = (Map<String, Object>) redisTemplate.opsForValue().get(key);
        
        if (timeConfig == null) {
            TrainingProgram program = studentCourseSelectionMapper.getTrainingProgramByStudentNo(studentNo);
                    
            if (program != null) {
                timeConfig = new HashMap<>();
                if (program.getCourseSelectionBegin() != null) {
                    timeConfig.put("start", program.getCourseSelectionBegin().getTime());
                }
                if (program.getCourseSelectionEnd() != null) {
                    timeConfig.put("end", program.getCourseSelectionEnd().getTime());
                }
                // 缓存 1 分钟
                redisTemplate.opsForValue().set(key, timeConfig, 1, java.util.concurrent.TimeUnit.MINUTES);
            }
        }
        
        if (timeConfig != null) {
            long now = System.currentTimeMillis();
            Object startObj = timeConfig.get("start");
            Object endObj = timeConfig.get("end");
            
            if (startObj != null && now < ((Number) startObj).longValue()) return "选课未开始";
            if (endObj != null && now > ((Number) endObj).longValue()) return "选课已结束";
        }
        return null;
    }

}
