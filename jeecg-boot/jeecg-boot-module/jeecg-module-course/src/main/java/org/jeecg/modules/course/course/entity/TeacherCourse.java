package org.jeecg.modules.course.course.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 教师课程安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Schema(description="教师课程安排")
@Data
@TableName("teacher_course")
public class TeacherCourse implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private java.lang.String id;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**课程号*/
	@Excel(name = "课程号", width = 15)
    @Schema(description = "课程号")
    private java.lang.String courseId;
	/**科目*/
	@Excel(name = "科目", width = 15, dicCode = "course")
    @Dict(dicCode = "course")
    @Schema(description = "科目")
    private java.lang.String course;
	/**教师工号*/
	@Excel(name = "教师工号", width = 15)
    @Schema(description = "教师工号")
    private java.lang.String teacherNo;
	/**课程学分*/
	@Excel(name = "课程学分", width = 15)
    @Schema(description = "课程学分")
    private java.math.BigDecimal courseCredit;
	/**课程容量*/
	@Excel(name = "课程容量", width = 15)
    @Schema(description = "课程容量")
    private java.lang.Integer capacity;
	/**课程类型*/
	@Excel(name = "课程类型", width = 15, dicCode = "course_type")
    @Dict(dicCode = "course_type")
    @Schema(description = "课程类型")
    private java.lang.Integer courseType;
}
