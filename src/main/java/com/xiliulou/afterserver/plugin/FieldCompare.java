package com.xiliulou.afterserver.plugin;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FieldCompare {

    // 字段名称
    String chineseName();
    // 类型映射
    String properties() default "";

}
