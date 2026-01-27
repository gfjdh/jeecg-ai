package org.jeecg.modules.student.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 学生信息传输对象 (Data Transfer Object)
 * 通常用于前端向后端传输数据，可能包含一些 Entity 中没有的字段，或者少一些字段
 * @Author: qssh
 * @Date: 2026-01-23
 * @Version: V1.0
 */
@Data
@Schema(name="StudentDTO", description="学生信息DTO")
public class StudentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private String id;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "出生日期")
    private Date birthday;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "年级")
    private String year;

    @Schema(description = "手机号")
    private String phone;
}
