package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FlavorMapper {
    /**
     * 批量增加菜品口味
     */
    void insertBatch(List<DishFlavor> flavors);
    /**
     * 删除菜品id对应的口味信息
     */
    @Delete("delete from dish_flavor where dish_id=#{DishId}")
    void deleteByDishId(Long DishId);

    /**
     *根据DishId查询对应的口味列表
     */
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
