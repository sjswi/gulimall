package com.atguigu.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * springSession的使用
 * 1、导入@EnableRedisHttpSession，
 *      1、添加了RedisIndexedSessionRepository这个组件，redis增删该查的封装类,封装了一个内部类RedisSession。这个类利用RedisOperation接口操作redis
 *      redisTemplate是RedisOperation接口的实现类
 *               RedisIndexedSessionRepository.getSession()获取session
 *      2、添加了RedisMessageListenerContainer监听器，
 *
 *  单点登陆：（核心，域名不同同步用户的信息）
 *
 *      1、需要一个中央认证服务器，
 *      2、其他系统登陆到认证服务器，登陆成功跳转
 *      3、只要有一个登陆，其他都不需要登陆(关键点在浏览器留下痕迹,即cookie存有一个标志，一般为token)
 *      4、全系统只有一个sso-sessionId,所有系统的域名都不相同
 * **/

@EnableRedisHttpSession //整合redis存储session
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
