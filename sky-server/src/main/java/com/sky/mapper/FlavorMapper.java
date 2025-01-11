package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlavorMapper {
    /**
     * 批量增加菜品口味
     */

    void insertBatch(List<DishFlavor> flavors);

}
