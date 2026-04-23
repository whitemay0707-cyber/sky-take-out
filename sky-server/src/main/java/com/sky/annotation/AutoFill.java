package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于表示某个方法需要进行公共字段填充处理

 */
@Target(ElementType.METHOD)//这个注解只能加在方法上面
@Retention(RetentionPolicy.RUNTIME)//@Retention(RetentionPolicy.RUNTIME) 表示该注解在运行时仍然保留，可以通过反射读取。
public @interface AutoFill {
    //数据库操作类型的枚举值 UPDATE INSERT
    OperationType value();//注解中定义的一个成员方法，用于指定注解的值。注解有一个必填属性 value
     //- 类型是 OperationType（通常是枚举）
}
