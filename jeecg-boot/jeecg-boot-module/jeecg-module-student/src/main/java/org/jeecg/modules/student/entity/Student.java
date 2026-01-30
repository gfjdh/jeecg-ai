package org.jeecg.modules.student.entity;

import java.util.Date;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.desensitization.annotation.SensitiveField;
import org.jeecg.common.desensitization.enums.SensitiveEnum;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 学生信息实体类
 * 继承 JeecgEntity 以复用通用字段（id, createBy, createTime, updateBy, updateTime）
 * @Author: qssh
 * @Date: 2026-01-23
 * @Version: V1.0
 */
@Data
@TableName("student_info") // 对应数据库表名
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true) // 支持链式调用 set 方法
@Schema(name="Student", description="学生信息")
public class Student extends JeecgEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 学号
     * 使用 @Excel 注解支持导入导出
     */
    @Excel(name = "学号", width = 15)
    @Schema(description = "学号")
    private String studentNo;

    /**
     * 姓名
     */
    @Excel(name = "姓名", width = 15)
    @Schema(description = "姓名")
    private String name;

    /**
     * 性别 (1:男 2:女)
     */
    @Excel(name = "性别", width = 15, dicCode = "sex") // dicCode对应数据字典
    @Schema(description = "性别")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 出生日期
     */
    @Excel(name = "出生日期", width = 25, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "出生日期")
    private Date birthday;

    /**
     * 专业
     */
    @Excel(name = "专业", width = 15, dicCode = "major")
    @Schema(description = "专业")
    @Dict(dicCode = "major")
    private String major;

    /**
     * 班级
     */
    @Excel(name = "班级", width = 15)
    @Schema(description = "班级")
    private String className;

    /**
     * 年级
     */
    @Excel(name = "年级", width = 15)
    @Schema(description = "年级")   
    private String year;

    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @SensitiveField(type = SensitiveEnum.MOBILE_PHONE)
    @Schema(description = "手机号")
    private String phone;
}
