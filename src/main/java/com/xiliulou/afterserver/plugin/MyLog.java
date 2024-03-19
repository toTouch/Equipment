package com.xiliulou.afterserver.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义日志注解
@Target({ElementType.PARAMETER, ElementType.METHOD})//用于描述方法和参数
@Retention(RetentionPolicy.RUNTIME)//始终不会丢弃，运行期也保留该注解
public @interface MyLog {
    
    //模块名
    String value() default "";
    
}
