package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     */
    List<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     */
    @Select("select * from setmeal where id=#{id}")
    Setmeal getById(Long id);


    /**
     * 删除套餐
     */
    @Delete("delete from setmeal where id=#{id}")
    void delete(Long id);

    /**
     * 修改套餐
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
