package com.xiliulou.afterserver.config;

/**
 * @author Hardy
 * @date 2021/4/25 0025 18:24
 * @Description:
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author weiyanqiang
 * @title: SpringContextUtil
 * @description: spring工具类，用来获取bean
 * @date 2019/10/25 11:01
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static Object getBean(Class clazz) {
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String name, Class clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
