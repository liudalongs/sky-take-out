package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;
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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //1. 设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //2. 执行查询
        List<DishVO> list=dishMapper.pageQuery(dishPageQueryDTO);
        Page<DishVO> page= (Page<DishVO>) list;
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional //涉及多表操作，要加上事务管理
    public void deleteBatch(List<Long> ids) {
        //先判断当前菜品能否删除（状态为0停售可以删，状态为1起售不能删）
         for(Long id : ids) {
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                //当前有些菜品处于起售中，不允许此次删除菜品操作,抛个异常给前端
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
         }
        //判断当前是否有菜品是否关联了套餐，若有菜品关联了也不能删
         List<Long> list=setmealDishMapper.getSetmealIdsByDishIds(ids);
         if(list != null && list.size() > 0) {
             //说明当前菜品确实关联了套餐，不能删,抛个异常
             throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
         }
        //删除剩下的菜品
         for(Long id : ids) {
             //根据id删除菜品
             dishMapper.deleteById(id);
             //删除菜品关联的口味表数据
             flavorMapper.deleteByDishId(id);
         }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO=new DishVO();
        //先查dish表，把dish一些属性查出来
        Dish dish=dishMapper.getById(id);
        //再查flavor表，把对应的口味查出来
         List<DishFlavor> dishFlavors=flavorMapper.getByDishId(id);
        //再把上面查出来的属性封装到dishVO对象中去
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品以及口味
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品表
         Dish dish=new Dish();
         BeanUtils.copyProperties(dishDTO,dish);
         dishMapper.update(dish);
        //修改口味表(比较复杂，因为口味可能追加了，也可能删除了，也可能不变，因此可以全删再重新添加）
              //1、先删除该菜品的全部口味信息
              Long dishId=dishDTO.getId();
              flavorMapper.deleteByDishId(dishId);
              //2、再重新添加该菜品口味信息
              List<DishFlavor> flavors=dishDTO.getFlavors();
              if(flavors != null && flavors.size() > 0) {
                  //这里的flavors对象没有dishId的属性值，因为前端没有传过来，需手动添加
                  for(DishFlavor f : flavors) {
                      f.setDishId(dishId);
                  }
                  flavorMapper.insertBatch(flavors);
              }


    }
}
