package org.jeecg.modules.course.course.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecg.modules.course.course.entity.ClassTime;
import org.jeecg.modules.course.course.entity.TeacherCourse;
import org.jeecg.modules.course.course.vo.TeacherCoursePage;
import org.jeecg.modules.course.course.service.ITeacherCourseService;
import org.jeecg.modules.course.course.service.IClassTimeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 教师课程安排
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Tag(name="教师课程安排")
@RestController
@RequestMapping("/course/teacherCourse")
@Slf4j
public class TeacherCourseController {
	@Autowired
	private ITeacherCourseService teacherCourseService;
	@Autowired
	private IClassTimeService classTimeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param teacherCourse
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "教师课程安排-分页列表查询")
	@Operation(summary="教师课程安排-分页列表查询")
	@PermissionData(pageComponent="course/TeacherCourseList")
	@GetMapping(value = "/list")
	public Result<IPage<TeacherCourse>> queryPageList(TeacherCourse teacherCourse,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TeacherCourse> queryWrapper = QueryGenerator.initQueryWrapper(teacherCourse, req.getParameterMap());
		Page<TeacherCourse> page = new Page<TeacherCourse>(pageNo, pageSize);
		IPage<TeacherCourse> pageList = teacherCourseService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param teacherCoursePage
	 * @return
	 */
	@AutoLog(value = "教师课程安排-添加")
	@Operation(summary="教师课程安排-添加")
    @RequiresPermissions("course:teacher_course:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TeacherCoursePage teacherCoursePage) {
		TeacherCourse teacherCourse = new TeacherCourse();
		BeanUtils.copyProperties(teacherCoursePage, teacherCourse);
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (!SecurityUtils.getSubject().isPermitted("teacherCourse:edit")) {
			teacherCourse.setTeacherNo(sysUser.getUsername());
		}
		teacherCourseService.saveMain(teacherCourse, teacherCoursePage.getClassTimeList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param teacherCoursePage
	 * @return
	 */
	@AutoLog(value = "教师课程安排-编辑")
	@Operation(summary="教师课程安排-编辑")
    @RequiresPermissions("course:teacher_course:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TeacherCoursePage teacherCoursePage) {
		TeacherCourse teacherCourse = new TeacherCourse();
		BeanUtils.copyProperties(teacherCoursePage, teacherCourse);
		TeacherCourse teacherCourseEntity = teacherCourseService.getById(teacherCourse.getId());
		if(teacherCourseEntity==null) {
			return Result.error("未找到对应数据");
		}
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (!SecurityUtils.getSubject().isPermitted("teacherCourse:edit")) {
			teacherCourse.setTeacherNo(sysUser.getUsername());
		}
		teacherCourseService.updateMain(teacherCourse, teacherCoursePage.getClassTimeList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "教师课程安排-通过id删除")
	@Operation(summary="教师课程安排-通过id删除")
    @RequiresPermissions("course:teacher_course:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		teacherCourseService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "教师课程安排-批量删除")
	@Operation(summary="教师课程安排-批量删除")
    @RequiresPermissions("course:teacher_course:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.teacherCourseService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "教师课程安排-通过id查询")
	@Operation(summary="教师课程安排-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TeacherCourse> queryById(@RequestParam(name="id",required=true) String id) {
		TeacherCourse teacherCourse = teacherCourseService.getById(id);
		if(teacherCourse==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(teacherCourse);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "课程时间安排通过主表ID查询")
	@Operation(summary="课程时间安排主表ID查询")
	@GetMapping(value = "/queryClassTimeByMainId")
	public Result<List<ClassTime>> queryClassTimeListByMainId(@RequestParam(name="id",required=true) String id) {
		TeacherCourse teacherCourse = teacherCourseService.getById(id);
		List<ClassTime> classTimeList = new ArrayList<>();
		if(teacherCourse!=null){
			classTimeList = classTimeService.selectByMainId(teacherCourse.getCourseId());
		}
		return Result.OK(classTimeList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param teacherCourse
    */
    @RequiresPermissions("course:teacher_course:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TeacherCourse teacherCourse) {

      // Step.1 组装查询条件查询数据
      QueryWrapper<TeacherCourse> queryWrapper = QueryGenerator.initQueryWrapper(teacherCourse, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<TeacherCourse> teacherCourseList = teacherCourseService.list(queryWrapper);

      // Step.3 组装pageList
      List<TeacherCoursePage> pageList = new ArrayList<TeacherCoursePage>();
      for (TeacherCourse main : teacherCourseList) {
          TeacherCoursePage vo = new TeacherCoursePage();
          BeanUtils.copyProperties(main, vo);
          List<ClassTime> classTimeList = classTimeService.selectByMainId(main.getCourseId());
          vo.setClassTimeList(classTimeList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "教师课程安排列表");
      mv.addObject(NormalExcelConstants.CLASS, TeacherCoursePage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("教师课程安排数据", "导出人:"+sysUser.getRealname(), "教师课程安排", ExcelType.XSSF));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("course:teacher_course:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<TeacherCoursePage> list = ExcelImportUtil.importExcel(file.getInputStream(), TeacherCoursePage.class, params);
              for (TeacherCoursePage page : list) {
                  TeacherCourse po = new TeacherCourse();
                  BeanUtils.copyProperties(page, po);
                  teacherCourseService.saveMain(po, page.getClassTimeList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

}
