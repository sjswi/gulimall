package com.atguigu.gulimall.seckill;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 秒杀商品流量太大，应该提前将商品数据放到缓存中，且操作时也应该只操作缓存而不要直接操作数据库
 *
 *
 * 整合sentinel
 *  1、导入spring-cloud-starter-alibaba-sentinel
 *  2、下载sentinel的控制台
 *  3、配置文件中配置  sentinel:
 *       transport:
 *         dashboard: localhost:8333
 *         port: 8719
 *  4、在控制台调整参数，参数在服务重启后失败
 * 每个微服务导入actuator并配置management.endpoints.web.exposure.include=*
 *
 * feign的调用保护
 *      1、调用方熔断保护，在feign调用时指定Fallback
 *      2、在dashboard手动设置熔断
 *      3、超大流量时，必须牺牲一些远程服务，在服务的提供方指定降级策略
 *          提供方还是在运行但是不运行业务逻辑而是返回熔断数据
 *
 * 自定义受保护的资源
 *      1、try(指定资源){}catch(){抛出异常}
 *      2、@SentinelResource(value="", blockHandler="方法名")
 * **/
//@EnableRabbit 接收消息必须要添加，否则可以不用
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class, args);
    }

}
