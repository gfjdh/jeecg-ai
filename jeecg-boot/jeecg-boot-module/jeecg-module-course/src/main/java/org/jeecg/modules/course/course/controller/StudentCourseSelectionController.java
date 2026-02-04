package org.jeecg.modules.course.course.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.course.course.entity.StudentCourseSelection;
import org.jeecg.modules.course.course.service.IStudentCourseSelectionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 学生选课表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Tag(name="学生选课表")
@RestController
@RequestMapping("/course/studentCourseSelection")
@Slf4j
public class StudentCourseSelectionController extends JeecgController<StudentCourseSelection, IStudentCourseSelectionService> {
	@Autowired
	private IStudentCourseSelectionService studentCourseSelectionService;
	
	/**
	 * 分页列表查询
	 *
	 * @param studentCourseSelection
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "学生选课表-分页列表查询")
	@Operation(summary="学生选课表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StudentCourseSelection>> queryPageList(StudentCourseSelection studentCourseSelection,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {


        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("courseType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("studyStatus", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<StudentCourseSelection> queryWrapper = QueryGenerator.initQueryWrapper(studentCourseSelection, req.getParameterMap(),customeRuleMap);
		Page<StudentCourseSelection> page = new Page<StudentCourseSelection>(pageNo, pageSize);
		IPage<StudentCourseSelection> pageList = studentCourseSelectionService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param studentCourseSelection
	 * @return
	 */
	@AutoLog(value = "学生选课表-添加")
	@Operation(summary="学生选课表-添加")
	@RequiresPermissions("course:student_course_selection:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody StudentCourseSelection studentCourseSelection) {
		studentCourseSelectionService.save(studentCourseSelection);

		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param studentCourseSelection
	 * @return
	 */
	@AutoLog(value = "学生选课表-编辑")
	@Operation(summary="学生选课表-编辑")
	@RequiresPermissions("course:student_course_selection:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StudentCourseSelection studentCourseSelection) {
		studentCourseSelectionService.updateById(studentCourseSelection);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "学生选课表-通过id删除")
	@Operation(summary="学生选课表-通过id删除")
	@RequiresPermissions("course:student_course_selection:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		studentCourseSelectionService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "学生选课表-批量删除")
	@Operation(summary="学生选课表-批量删除")
	@RequiresPermissions("course:student_course_selection:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.studentCourseSelectionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "学生选课表-通过id查询")
	@Operation(summary="学生选课表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StudentCourseSelection> queryById(@RequestParam(name="id",required=true) String id) {
		StudentCourseSelection studentCourseSelection = studentCourseSelectionService.getById(id);
		if(studentCourseSelection==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(studentCourseSelection);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param studentCourseSelection
    */
    @RequiresPermissions("course:student_course_selection:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StudentCourseSelection studentCourseSelection) {
        return super.exportXls(request, studentCourseSelection, StudentCourseSelection.class, "学生选课表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("course:student_course_selection:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, StudentCourseSelection.class);
    }

}
