package org.jeecg.modules.course.course.vo;

import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 学生选课学分统计
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Data
@Schema(description="学生选课学分统计")
public class StudentCourseSummaryVO {
    
    @Schema(description = "已选必修学分")
    private BigDecimal selectedCompulsory;
    
    @Schema(description = "已选选修学分")
    private BigDecimal selectedElective;
    
    @Schema(description = "已修必修学分")
    private BigDecimal completedCompulsory;
    
    @Schema(description = "已修选修学分")
    private BigDecimal completedElective;

    public StudentCourseSummaryVO() {
        this.selectedCompulsory = BigDecimal.ZERO;
        this.selectedElective = BigDecimal.ZERO;
        this.completedCompulsory = BigDecimal.ZERO;
        this.completedElective = BigDecimal.ZERO;
    }
}
