package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private FlavorMapper flavorMapper;
    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Transactional //事务管理，因为这里要操作两张表，怕不一致造成数据库有问题
    public void saveWithFlavor(DishDTO dishDTO) {
         //向菜品表加入数据
         Dish dish = new Dish();
         BeanUtils.copyProperties(dishDTO, dish);//把dishDTO属性复制给dish对象
         dishMapper.insert(dish);
         //获得插入后dish对象的id属性值
         Long dishId=dish.getId();

         List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0) {
            for(DishFlavor f : flavors) { //遍历集合，把dishId赋值进去给DishFlavor对象
                f.setDishId(dishId);
            }
            //向口味表插入0或1条或多条数据
           flavorMapper.insertBatch(flavors);//批量添加数据
        }
    }
}
