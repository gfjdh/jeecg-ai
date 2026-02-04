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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 培养方案表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Data
@TableName("training_program")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="培养方案表")
public class TrainingProgram implements Serializable {
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
	/**专业ID*/
	@Excel(name = "专业ID", width = 15, dicCode = "major")
    @Dict(dicCode = "major")
    @Schema(description = "专业ID")
    private java.lang.Integer majorId;
	/**适用入学年份*/
	@Excel(name = "适用入学年份", width = 15)
    @Schema(description = "适用入学年份")
    private java.lang.String startYear;
	/**选修学分要求*/
	@Excel(name = "选修学分要求", width = 15)
    @Schema(description = "选修学分要求")
    private java.math.BigDecimal requiredElectiveCredits;
	/**必修学分要求*/
	@Excel(name = "必修学分要求", width = 15)
    @Schema(description = "必修学分要求")
    private java.math.BigDecimal requiredMajorCredits;
	/**选课开始时间*/
	@Excel(name = "选课开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "选课开始时间")
    private java.util.Date courseSelectionBegin;
	/**选课结束时间*/
	@Excel(name = "选课结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "选课结束时间")
    private java.util.Date courseSelectionEnd;
}
