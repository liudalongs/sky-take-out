package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据dishids查询是否有关联的套餐,并返回该套餐的ids
     */
    List<Long> getSetmealIdsByDishIds(List<Long> DishIds);

    /**
     *把套餐和对应的菜品关系插入setmeal_dish表中
     */
    @Insert("insert into setmeal_dish(setmeal_id, dish_id, name, price, copies) "+
     "values"+ "(#{setmealId},#{dishId},#{name},#{price},#{copies})")
    void insert(SetmealDish SetmealDish);

    /**
     * 删除套餐id相关联的菜品信息
     */
    @Delete("delete from setmeal_dish where setmeal_id=#{SetmealId}")
    void deleteBySetmealId(Long SetmealId);

    /**
     * 根据setmeal_id查询所有关联菜品
     */
    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getSetmealDishsBySetmealId(Long id);

    /**
     * 批量插入setmealdish
     */

    void  insertBatch(List<SetmealDish> SetmealDishs);
}
