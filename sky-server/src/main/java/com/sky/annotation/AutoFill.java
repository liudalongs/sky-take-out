package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */
@Target(ElementType.METHOD)//表示该注解可以用于方法上。这意味着只有在方法上使用这个注解才是有效的，而在其他位置使用会导致编译错误。
@Retention(RetentionPolicy.RUNTIME)//表示这个注解在运行时仍然可用，这个注解的信息会保存到运行时环境中，允许在运行时通过反射访问这个注解。
public @interface AutoFill {
    //指定数据库操作类型 UPDATE INSERT
    OperationType value();
}
