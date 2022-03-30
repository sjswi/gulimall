package com.atguigu.gulimall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: gulimall
 * @description: 解决在同一config中循环依赖的问题
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 14:58
 **/
@Configuration
public class MyRabbitConverter {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
