package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j //注解在类上，为类提供一个属性名为log的log4j日志对象
@Component
@Aspect //说明这个类和AOP有关
public class AutoFillAspect {
    /**
     * 设置切入点
     */
    //只有com.sky.mapper包下的所有类下的所有方法并且加了AutoFill注解的方法才有效
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint)  {
        log.info("开始进行公共字段填充。。。。");
        //获取当前被拦截的方法上的数据库操作类型UPDATE 或者INSERT
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象,调用 getSignature() 方法，可以获取与当前连接点（join point）相关联的方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象,通过 signature.getMethod() 可以获取与当前方法签名相关联的方法
        OperationType operationType = autoFill.value();//获得数据库操作类型,从 AutoFill 注解中获取 value 属性的值，并将其赋值给 operationType 变量

        //获取当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args==null || args.length==0){
            return;  //若参数为空，直接退出
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime localDateTime = LocalDateTime.now();//当前时间
        Long id= BaseContext.getCurrentId();//当前id

        //根据当前不同的操作类型
        if(operationType==OperationType.INSERT){
            try {
                Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                //通过反射给对象赋值
                setCreateTime.invoke(entity,localDateTime);
                setUpdateTime.invoke(entity,localDateTime);
                setCreateUser.invoke(entity,id);
                setUpdateUser.invoke(entity,id);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(operationType==OperationType.UPDATE){
            try {
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                //通过反射给对象赋值
                setUpdateTime.invoke(entity,localDateTime);
                setUpdateUser.invoke(entity,id);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

