package org.jeecg.modules.course.course.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.course.course.entity.ClassCourseType;
import org.jeecg.modules.course.course.service.IClassCourseTypeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 班级选必修信息表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Tag(name="班级选必修信息表")
@RestController
@RequestMapping("/course/classCourseType")
@Slf4j
public class ClassCourseTypeController extends JeecgController<ClassCourseType, IClassCourseTypeService> {
	@Autowired
	private IClassCourseTypeService classCourseTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param classCourseType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "班级选必修信息表-分页列表查询")
	@Operation(summary="班级选必修信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ClassCourseType>> queryPageList(ClassCourseType classCourseType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {


        QueryWrapper<ClassCourseType> queryWrapper = QueryGenerator.initQueryWrapper(classCourseType, req.getParameterMap());
		Page<ClassCourseType> page = new Page<ClassCourseType>(pageNo, pageSize);
		IPage<ClassCourseType> pageList = classCourseTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param classCourseType
	 * @return
	 */
	@AutoLog(value = "班级选必修信息表-添加")
	@Operation(summary="班级选必修信息表-添加")
	@RequiresPermissions("course:class_course_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ClassCourseType classCourseType) {
		classCourseTypeService.save(classCourseType);

		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param classCourseType
	 * @return
	 */
	@AutoLog(value = "班级选必修信息表-编辑")
	@Operation(summary="班级选必修信息表-编辑")
	@RequiresPermissions("course:class_course_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ClassCourseType classCourseType) {
		classCourseTypeService.updateById(classCourseType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "班级选必修信息表-通过id删除")
	@Operation(summary="班级选必修信息表-通过id删除")
	@RequiresPermissions("course:class_course_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		classCourseTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "班级选必修信息表-批量删除")
	@Operation(summary="班级选必修信息表-批量删除")
	@RequiresPermissions("course:class_course_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.classCourseTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "班级选必修信息表-通过id查询")
	@Operation(summary="班级选必修信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ClassCourseType> queryById(@RequestParam(name="id",required=true) String id) {
		ClassCourseType classCourseType = classCourseTypeService.getById(id);
		if(classCourseType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(classCourseType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param classCourseType
    */
    @RequiresPermissions("course:class_course_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ClassCourseType classCourseType) {
        return super.exportXls(request, classCourseType, ClassCourseType.class, "班级选必修信息表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("course:class_course_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ClassCourseType.class);
    }

}
