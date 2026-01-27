package org.jeecg.modules.student.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * @Description: 学生成绩信息传输对象 (Data Transfer Object)
 * @Author: qssh
 * @Date: 2026-01-27
 * @Version: V1.0
 */
@Data
@Schema(name="StudentGradeDTO", description="学生成绩信息DTO")
public class StudentGradeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private String id;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "课程")
    private String course;

    @Schema(description = "成绩")
    private Double score;
}
