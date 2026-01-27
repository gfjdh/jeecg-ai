package org.jeecg.modules.student.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.student.entity.Student;
import org.jeecg.modules.student.service.IStudentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Description: 学生信息管理 Controller
 * 继承 JeecgController，泛型指定 Entity 和 Service
 * 复用底层提供的 common API 能力
 * @Author: qssh
 * @Date: 2026-01-26
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "学生信息管理")
@RestController
@RequestMapping("/student")
public class StudentController extends JeecgController<Student, IStudentService> {
    
    @Autowired
    private IStudentService studentService; // 注入学生信息 Service

    /**
     * 分页列表查询
     * @param student 查询条件实体
     * @param pageNo 当前页码
     * @param pageSize 每页大小
     * @param req HttpServletRequest
     * @return Result<IPage<Student>>
     */
    // @SensitiveEncode (手动处理脱敏，暂时注释掉注解)
    @Operation(summary = "学生信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Student>> queryPageList(Student student,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                HttpServletRequest req) {
        IPage<Student> pageList = studentService.queryPageList(student, pageNo, pageSize, req.getParameterMap());
        return Result.ok(pageList);
    }

    /**
     * 添加学生信息
     * @param student
     * @return
     */
    @AutoLog(value = "学生信息-添加", operateType = CommonConstant.OPERATE_TYPE_2)
    @Operation(summary = "学生信息-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Student student) {
        studentService.save(student);
        return Result.ok("添加成功！");
    }

    /**
     * 批量添加
     * @param students
     * @return
     */
    @AutoLog(value = "学生信息-批量添加", operateType = CommonConstant.OPERATE_TYPE_2)
    @Operation(summary = "学生信息-批量添加")
    @PostMapping(value = "/addBatch")
    public Result<String> addBatch(@RequestBody java.util.List<Student> students) {
        studentService.saveBatch(students);
        return Result.ok("批量添加成功！");
    }

    /**
     * 编辑学生信息
     * @param student
     * @return
     */
    @AutoLog(value = "学生信息-编辑", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "学生信息-编辑")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "学生信息-通过id删除", operateType = CommonConstant.OPERATE_TYPE_4)
    @Operation(summary = "学生信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        studentService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @Operation(summary = "学生信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.studentService.deleteBatch(ids);
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    @Operation(summary = "学生信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Student> queryById(@RequestParam(name="id",required=true) String id) {
        Student student = studentService.getById(id);
        if(student==null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(student);
    }

    /**
     * 根据学号检查学生是否存在
     * @param studentNo
     * @return
     */
    @Operation(summary = "学生信息-根据学号检查是否存在")
    @GetMapping(value = "/checkByStudentNo")
    public Result<Boolean> checkByStudentNo(@RequestParam(name="studentNo", required=true) String studentNo) {
        long count = studentService.count(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, studentNo));
        return Result.ok(count > 0);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param student
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Student student) {
        return super.exportXls(request, student, Student.class, "学生信息");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Student.class);
    }
}
