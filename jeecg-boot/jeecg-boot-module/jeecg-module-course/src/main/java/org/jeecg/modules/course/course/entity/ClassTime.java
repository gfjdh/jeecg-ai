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
 * @Description: 课程时间安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Schema(description="课程时间安排")
@Data
@TableName("class_time")
public class ClassTime implements Serializable {
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
	/**对应课程号*/
    @Schema(description = "对应课程号")
    private java.lang.String courseId;
	/**weekday*/
	@Excel(name = "weekday", width = 15, dicCode = "weekday")
    @Dict(dicCode = "weekday")
    @Schema(description = "weekday")
    private java.lang.Integer weekday;
	/**location*/
	@Excel(name = "location", width = 15)
    @Schema(description = "location")
    private java.lang.String location;
	/**start_section*/
	@Excel(name = "start_section", width = 15)
    @Schema(description = "start_section")
    private java.lang.Integer startSection;
	/**end_section*/
	@Excel(name = "end_section", width = 15)
    @Schema(description = "end_section")
    private java.lang.Integer endSection;
}
