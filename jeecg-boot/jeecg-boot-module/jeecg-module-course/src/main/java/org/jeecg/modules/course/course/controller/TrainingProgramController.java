package org.jeecg.modules.course.course.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.course.course.entity.TrainingProgram;
import org.jeecg.modules.course.course.service.ITrainingProgramService;

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
 * @Description: 培养方案表
 * @Author: jeecg-boot
 * @Date:   2026-02-04
 * @Version: V1.0
 */
@Tag(name="培养方案表")
@RestController
@RequestMapping("/course/trainingProgram")
@Slf4j
public class TrainingProgramController extends JeecgController<TrainingProgram, ITrainingProgramService> {
	@Autowired
	private ITrainingProgramService trainingProgramService;
	
	/**
	 * 分页列表查询
	 *
	 * @param trainingProgram
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "培养方案表-分页列表查询")
	@Operation(summary="培养方案表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainingProgram>> queryPageList(TrainingProgram trainingProgram,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {


        QueryWrapper<TrainingProgram> queryWrapper = QueryGenerator.initQueryWrapper(trainingProgram, req.getParameterMap());
		Page<TrainingProgram> page = new Page<TrainingProgram>(pageNo, pageSize);
		IPage<TrainingProgram> pageList = trainingProgramService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param trainingProgram
	 * @return
	 */
	@AutoLog(value = "培养方案表-添加")
	@Operation(summary="培养方案表-添加")
	@RequiresPermissions("course:training_program:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TrainingProgram trainingProgram) {
		trainingProgramService.save(trainingProgram);

		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param trainingProgram
	 * @return
	 */
	@AutoLog(value = "培养方案表-编辑")
	@Operation(summary="培养方案表-编辑")
	@RequiresPermissions("course:training_program:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TrainingProgram trainingProgram) {
		trainingProgramService.updateById(trainingProgram);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培养方案表-通过id删除")
	@Operation(summary="培养方案表-通过id删除")
	@RequiresPermissions("course:training_program:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		trainingProgramService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培养方案表-批量删除")
	@Operation(summary="培养方案表-批量删除")
	@RequiresPermissions("course:training_program:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.trainingProgramService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "培养方案表-通过id查询")
	@Operation(summary="培养方案表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TrainingProgram> queryById(@RequestParam(name="id",required=true) String id) {
		TrainingProgram trainingProgram = trainingProgramService.getById(id);
		if(trainingProgram==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(trainingProgram);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param trainingProgram
    */
    @RequiresPermissions("course:training_program:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrainingProgram trainingProgram) {
        return super.exportXls(request, trainingProgram, TrainingProgram.class, "培养方案表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("course:training_program:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TrainingProgram.class);
    }

}
