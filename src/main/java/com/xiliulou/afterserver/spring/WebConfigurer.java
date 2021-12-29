package com.xiliulou.afterserver.spring;

import com.xiliulou.afterserver.Interceptor.AuthInterceptorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域拦截器需放在最上面
//		registry.addInterceptor(getAuthInterceptorAdapter()).addPathPatterns("/**");
        //拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录

        registry.addInterceptor(getAuthInterceptorAdapter()).addPathPatterns("/**");
    }

    @Bean
    public AuthInterceptorAdapter getAuthInterceptorAdapter() {
        return new AuthInterceptorAdapter();
    }


}
