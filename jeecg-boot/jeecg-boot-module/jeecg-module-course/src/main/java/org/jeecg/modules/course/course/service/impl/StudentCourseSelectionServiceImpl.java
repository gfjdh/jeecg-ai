package org.jeecg.modules.course.course.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.course.course.component.CourseRushConsumer;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.entity.TrainingProgram;
import org.jeecg.modules.course.course.mapper.StudentCourseSelectionMapper;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.entity.StudentSchedule;
import java.util.Set;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 学生选课表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Service
public class StudentCourseSelectionServiceImpl extends ServiceImpl<StudentCourseSelectionMapper, StudentCourseSelection> implements IStudentCourseSelectionService {

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
    private StudentCourseSelectionMapper studentCourseSelectionMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ITeacherCourseService teacherCourseService;

    @Autowired
    private IClassTimeService classTimeService;

    @Override
    public Result<String> rush(String courseId, String studentNo) {
        // 验证选课时间
        String timeError = validateSelectionTime(studentNo);
        if (timeError != null) {
            return Result.error(timeError);
        }

        // 1. 检查队列限制
        TeacherCourse course = teacherCourseService.getTeacherCourse(courseId);
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
        stringRedisTemplate.opsForValue().set(statusKey, "PENDING", 1, TimeUnit.MINUTES);        // 设置初始状态
        
        // 发送到全局处理队列
        redisTemplate.opsForList().leftPush(CourseRushConsumer.GLOBAL_QUEUE_KEY, payload);
        
        return Result.OK("进入排队");
    }

    /**
     * 校验选课时间
     */
    @Override
    @SuppressWarnings("unchecked")
    public String validateSelectionTime(String studentNo) {
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
                redisTemplate.opsForValue().set(key, timeConfig, 1, TimeUnit.MINUTES);
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

    @Override
    @SuppressWarnings("unchecked")
    public String checkTimeConflict(String studentNo, String courseId) {
        String key = "course:student:time:" + studentNo;
        Set<String> selectedTimes = (Set<String>) redisTemplate.opsForValue().get(key);
        
        if (selectedTimes == null) {
            selectedTimes = new HashSet<>();
            List<StudentSchedule> schedules = studentCourseSelectionMapper.getStudentSchedule(studentNo);
            if (schedules != null) {
                for (StudentSchedule schedule : schedules) {
                    if (schedule.getWeekday() != null && schedule.getStartSection() != null && schedule.getEndSection() != null) {
                        for (int i = schedule.getStartSection(); i <= schedule.getEndSection(); i++) {
                            selectedTimes.add(schedule.getWeekday() + "-" + i);
                        }
                    }
                }
            }
            redisTemplate.opsForValue().set(key, selectedTimes, 10, TimeUnit.MINUTES);
        }
        
        List<ClassTime> newCourseTimes = classTimeService.selectByMainId(courseId);
        if (newCourseTimes != null) {
            for (ClassTime ct : newCourseTimes) {
                if (ct.getWeekday() != null && ct.getStartSection() != null && ct.getEndSection() != null) {
                    for (int i = ct.getStartSection(); i <= ct.getEndSection(); i++) {
                        if (selectedTimes.contains(ct.getWeekday() + "-" + i)) {
                            return "conflict with " + courseId;
                        }
                    }
                }
            }
        }
        
        return null;
    }

    @Override
    public Integer getOverrideCourseType(String studentNo, String courseId) {
        String key = "course:override:map:" + courseId;
        
        // 1. 检查缓存是否存在
        if (redisTemplate.hasKey(key)) {
            Object val = redisTemplate.opsForHash().get(key, studentNo);
            if (val != null) {
                // 有覆盖配置，安全转换类型
                if (val instanceof Integer) {
                    return (Integer) val;
                } else if (val instanceof Number) {
                    return ((Number) val).intValue();
                } else if (val instanceof String) {
                    try {
                        return Integer.parseInt((String) val);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            } else {
                // 缓存存在但无此学生配置，说明无覆盖，返回null（即原课程类型）
                return null;
            }
        }

        // 2. 缓存未命中，加载全量数据
        List<Map<String, Object>> overrides = studentCourseSelectionMapper.getStudentOverridesByCourseId(courseId);
        Map<String, Object> cacheMap = new HashMap<>();
        
        // 放入一个标记键，防止空Map无法存入Redis leading to cache penetration
        cacheMap.put("_loaded", "1");
        
        if (overrides != null && !overrides.isEmpty()) {
            for (Map<String, Object> item : overrides) {
                String sNo = (String) item.get("studentNo");
                Object cVal = item.get("courseType");
                if (sNo != null && cVal != null) {
                   // 确保类型安全地转换为 Integer，避免因数据异常导致缓存无法使用
                   if (cVal instanceof Integer) {
                       cacheMap.put(sNo, cVal);
                   } else if (cVal instanceof Number) {
                       cacheMap.put(sNo, ((Number) cVal).intValue());
                   } else {
                       try {
                           cacheMap.put(sNo, Integer.parseInt(cVal.toString()));
                       } catch (NumberFormatException e) {}
                   }
                }
            }
        }
        
        // 3. 写入缓存，过期时间10分钟
        redisTemplate.opsForHash().putAll(key, cacheMap);
        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        
        // 4. 返回当前请求的结果
        Object currentVal = cacheMap.get(studentNo);
        if (currentVal instanceof Integer) {
            return (Integer) currentVal;
        } else if (currentVal instanceof Number) {
            return ((Number) currentVal).intValue();
        }
        return null;
    }
}
