package org.jeecg.modules.course.course.entity;

import java.io.Serializable;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Description: 学生课表实体(视图)
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Data
@TableName("student_schedule_view")
@Schema(description="学生课表实体")
public class StudentSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private java.lang.String id;

    @Schema(description = "课程号")
    private java.lang.String courseId;

    /**
     * 科目 (字典)
     */
    @Dict(dicCode = "course")
    @Schema(description = "科目")
    private java.lang.String course;
    
    @Schema(description = "教师")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private java.lang.String teacherNo;

    @Schema(description = "学分")
    private java.lang.Double courseCredit;

    @Schema(description = "已选人数")
    private java.lang.Integer selectedCount;

    @Schema(description = "容量")
    private java.lang.Integer capacity;

    /**
     * 星期 (字典)
     */
    @Dict(dicCode = "weekday")
    @Schema(description = "星期")
    private java.lang.Integer weekday;

    @Schema(description = "开始节次")
    private java.lang.Integer startSection;

    @Schema(description = "结束节次")
    private java.lang.Integer endSection;

    /**
     * 课程类型 (字典)
     */
    @Dict(dicCode = "course_type")
    @Schema(description = "课程类型")
    private java.lang.Integer courseType;

    @Schema(description = "地点")
    private java.lang.String location;
}
