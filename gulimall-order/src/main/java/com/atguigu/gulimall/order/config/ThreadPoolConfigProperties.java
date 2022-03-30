package com.atguigu.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: gulimall
 * @description: 线程池配置
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-16 14:25
 **/
@Component
@Data
@ConfigurationProperties(prefix = "gulimall.thread")
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
