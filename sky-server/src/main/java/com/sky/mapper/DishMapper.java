package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     *新增菜品
     */
    @AutoFill(OperationType.INSERT) //自定义注解，自动填充这些字段
    void insert(Dish dish);

    /**
     * 菜品分页查询
     */
   List<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     */

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);
    /**
     * 根据主键id删除菜品
     */
    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);

    /**
     * 修改菜品
     */
     @AutoFill(OperationType.UPDATE)
     void update(Dish dish);

    /**
     * 根据分类id查询菜品
     */
    @Select("select * from dish where category_id=#{categoryId}")
    List<Dish> getDishByCategoryId(Long categoryId);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}
