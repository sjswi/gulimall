package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @program: gulimall
 * @description: Redisson的配置文件
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-09 17:50
 **/
//可重入锁，假设有A，B两个方法，A持有一个锁，且A调用B，B也需要A的那个锁，如果该锁是可重入锁，则B可以获得锁并执行
//若不是可重入锁，则会陷入死锁
@Configuration
public class MyRedissonConfig {
    @Bean
    RedissonClient redisson() throws IOException{
//        创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://101.201.76.21:6379").setPassword("pqhyxb").setTimeout(Integer.MAX_VALUE);

        return Redisson.create(config);
    }
}
