package com.atguigu.gulimall.order.config;

import com.atguigu.gulimall.order.inteceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: gulimall
 * @description: 拦截器注册
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:07
 **/
@Configuration
public class OrderWebConfiguration implements WebMvcConfigurer {
    @Autowired
    LoginUserInterceptor loginUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
