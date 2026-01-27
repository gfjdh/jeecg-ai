package org.jeecg.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.system.base.entity.JeecgEntity;

/**
 * @Description: 学生成绩信息
 * @Author: qssh
 * @Date: 2026-01-27
 * @Version: V1.0
 */
@Data
@TableName("student_grade")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name="StudentGrade", description="学生成绩信息")
public class StudentGrade extends JeecgEntity {
    private static final long serialVersionUID = 1L;

	/**学号*/
	@Excel(name = "学号", width = 15)
    @Schema(description = "学号")
    private java.lang.String studentNo;

	/**课程*/
	@Excel(name = "课程", width = 15)
    @Schema(description = "课程")
    @Dict(dicCode = "course")
    private java.lang.String course;

	/**成绩*/
	@Excel(name = "成绩", width = 15)
    @Schema(description = "成绩")
    private java.lang.Double score;
}
