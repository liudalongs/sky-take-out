package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags="套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     */
    @PostMapping
    @ApiOperation(value="新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐，{}",setmealDTO);
        setmealService.saveSetmealWithSetmealDishs(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    @ApiOperation(value="套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询，{}",setmealPageQueryDTO);
        PageResult pageResult =setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    @ApiOperation(value="删除套餐")
    public Result delete(@RequestParam List<Long> ids){

        log.info("删除套餐，{}",ids);
        setmealService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     */
    @GetMapping("/{id}")
    @ApiOperation(value="根据id查询套餐")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id){
        log.info("根据id查询套餐,{}",id);
       SetmealVO setmealVO=setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}
