package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
   @Autowired
   private SetmealMapper setmealMapper;
   @Autowired
   private SetmealDishMapper setmealDishMapper;
   @Autowired
   private CategoryMapper categoryMapper;
   @Autowired
   private DishMapper dishMapper;
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    public void saveSetmealWithSetmealDishs(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //把setmealDTO相同属性赋值给setmeal
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();//获取插入setmeal表中的主键id
        //再把套餐所含菜品信息放入setmeal_dish表中去
        List<SetmealDish> list=setmealDTO.getSetmealDishes();
        if(list!=null&&list.size()>0){
           for(SetmealDish setmealDish:list){
               setmealDish.setSetmealId(setmealId);//把setmeal表中的主键id赋值给每一项
              setmealDishMapper.insert(setmealDish);//然后一个个setmealdish插入进去
           }

        }

    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        List<SetmealVO> list=setmealMapper.page(setmealPageQueryDTO);

        Page<SetmealVO> page=(Page<SetmealVO>)list;
        PageResult pageResult=new PageResult(page.getTotal(),page.getResult());

        return pageResult;
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    public void delete(List<Long> ids) {
           for(Long id:ids){
               Setmeal setmeal = setmealMapper.getById(id);
               if(StatusConstant.ENABLE == setmeal.getStatus()){
                   //起售中的套餐不能删除
                   throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
               }
           }

          for(Long id:ids){
              //删除套餐表中的数据
              setmealMapper.delete(id);
              //删除套餐菜品关系表中的数据
              setmealDishMapper.deleteBySetmealId(id);
          }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Transactional
    public SetmealVO getSetmealById(Long id) {
        SetmealVO setmealVO=new SetmealVO();
        Setmeal setmeal = setmealMapper.getById(id);
        BeanUtils.copyProperties(setmeal, setmealVO);

        Long categoryId = setmeal.getCategoryId();//得到套餐对应的categoryId
        Category category=categoryMapper.getById(categoryId);

        setmealVO.setCategoryName(category.getName());

       List<SetmealDish>  list=setmealDishMapper.getSetmealDishsBySetmealId(id);

       setmealVO.setSetmealDishes(list);
       return setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //1、修改套餐表，执行update
        setmealMapper.update(setmeal);

        //套餐id
        Long setmealId = setmealDTO.getId();

        //2、删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //3、重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if(status == StatusConstant.ENABLE){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && dishList.size() > 0){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE == dish.getStatus()){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

}
