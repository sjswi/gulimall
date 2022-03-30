package com.atguigu.gulimall.cart.config;

import com.atguigu.gulimall.cart.inteceptor.CartInteceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: gulimall
 * @description: 拦截器的配置文件
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 13:29
 **/
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInteceptor()).addPathPatterns("/**");
    }
}
