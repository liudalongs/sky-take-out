package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据dishids查询是否有关联的套餐,并返回该套餐的ids
     */

    List<Long> getSetmealIdsByDishIds(List<Long> DishIds);
}
