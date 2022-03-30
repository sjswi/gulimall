package com.atguigu.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: gulimall
 * @description: feign的拦截器，用来调用cart的cookie信息获得用户信息
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-25 18:03
 **/
@Configuration
public class GuliFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //使用RequestContextHolder.getRequestAttributes获得,RequestContextHolder使用ThreadLoacl,因此再之后异步调用时会出现问题
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes!=null) {
                    HttpServletRequest request = requestAttributes.getRequest();//获得请求
//                同步cookie
                    String cookie = request.getHeader("Cookie");
                    template.header("Cookie", cookie);
//                System.out.println("feign远程调用之前的执行的函数");
                }
            }
        };
    }
}
