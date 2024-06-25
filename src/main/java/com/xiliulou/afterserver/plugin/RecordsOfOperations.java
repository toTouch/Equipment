package com.xiliulou.afterserver.plugin;

import com.xiliulou.afterserver.export.BusinessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义日志注解
@Target({ElementType.PARAMETER, ElementType.METHOD})//用于描述方法和参数
@Retention(RetentionPolicy.RUNTIME)//始终不会丢弃，运行期也保留该注解
public @interface RecordsOfOperations {

    //参数类型 Object / 标识字段
    String paramType() default "";

    //模块类型
    BusinessType type() default BusinessType.OTHER;
}
