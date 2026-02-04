package org.jeecg.modules.course.course.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 学生选课表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Data
@TableName("student_course_selection")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="学生选课表")
public class StudentCourseSelection implements Serializable {
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
	/**学生学号*/
	@Excel(name = "学生学号", width = 15)
    @Schema(description = "学生学号")
    private java.lang.String studentNo;
	/**课程号*/
	@Excel(name = "课程号", width = 15)
    @Schema(description = "课程号")
    private java.lang.String courseId;
	/**课程学分*/
	@Excel(name = "课程学分", width = 15)
    @Schema(description = "课程学分")
    private java.math.BigDecimal courseCredit;
	/**课程类型*/
	@Excel(name = "课程类型", width = 15, dicCode = "course_type")
	@Dict(dicCode = "course_type")
    @Schema(description = "课程类型")
    private java.lang.Integer courseType;
	/**修读状态*/
	@Excel(name = "修读状态", width = 15, dicCode = "study_status")
	@Dict(dicCode = "study_status")
    @Schema(description = "修读状态")
    private java.lang.Integer studyStatus;
}
