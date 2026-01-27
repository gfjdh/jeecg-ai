package org.jeecg.modules.student.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.student.entity.StudentGrade;
import org.jeecg.modules.student.service.IStudentGradeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;

/**
 * @Description: 学生成绩管理
 * @Author: qssh
 * @Date: 2026-01-27
 * @Version: V1.0
 */
@Tag(name = "学生成绩管理")
@RestController
@RequestMapping("/student/grade")
@Slf4j
public class StudentGradeController extends JeecgController<StudentGrade, IStudentGradeService> {
    
    @Autowired
    private IStudentGradeService studentGradeService;
    
    /**
     * 分页列表查询
     *
     * @param studentGrade
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @Operation(summary = "学生成绩-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StudentGrade>> queryPageList(StudentGrade studentGrade,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StudentGrade> queryWrapper = QueryGenerator.initQueryWrapper(studentGrade, req.getParameterMap());
        Page<StudentGrade> page = new Page<StudentGrade>(pageNo, pageSize);
        IPage<StudentGrade> pageList = studentGradeService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
    
    /**
     *   添加
     *
     * @param studentGrade
     * @return
     */
    @AutoLog(value = "学生成绩-添加", operateType = CommonConstant.OPERATE_TYPE_2)
    @Operation(summary = "学生成绩-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody StudentGrade studentGrade) {
        studentGradeService.save(studentGrade);
        return Result.ok("添加成功！");
    }

    /**
     * 批量添加
     * @param studentGrades
     * @return
     */
    @AutoLog(value = "学生成绩-批量添加", operateType = CommonConstant.OPERATE_TYPE_2)
    @Operation(summary = "学生成绩-批量添加")
    @PostMapping(value = "/addBatch")
    public Result<String> addBatch(@RequestBody java.util.List<StudentGrade> studentGrades) {
        studentGradeService.saveBatch(studentGrades);
        return Result.ok("批量添加成功！");
    }
    
    /**
     *  编辑
     *
     * @param studentGrade
     * @return
     */
    @AutoLog(value = "学生成绩-编辑", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "学生成绩-编辑")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody StudentGrade studentGrade) {
        studentGradeService.updateById(studentGrade);
        return Result.ok("编辑成功!");
    }
    
    /**
     *   通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "学生成绩-通过id删除", operateType = CommonConstant.OPERATE_TYPE_4)
    @Operation(summary = "学生成绩-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        studentGradeService.removeById(id);
        return Result.ok("删除成功!");
    }
    
    /**
     *  批量删除
     *
     * @param ids
     * @return
     */
    @Operation(summary = "学生成绩-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.studentGradeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }
    
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Operation(summary = "学生成绩-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<StudentGrade> queryById(@RequestParam(name="id",required=true) String id) {
        StudentGrade studentGrade = studentGradeService.getById(id);
        if(studentGrade==null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(studentGrade);
    }
}
