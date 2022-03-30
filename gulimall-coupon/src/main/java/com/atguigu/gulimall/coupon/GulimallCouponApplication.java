package com.atguigu.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
* nacos作为配置中心
* 1、引入依赖
* 2、创建bootstrap.properties文件，
* spring.application.name=gulimall-coupon
*    spring.cloud.nacos.config.server-addr=127.0.0.1:8848
* 3、需要给配置中心默认添加一个数据集，dataID, 默认规则应用名.properties
* 4、给配置文件写配置
* 5、动态获取配置
*   @RefreshScope 动态刷新
*   @Value获取配置信息
* 6、配置中心的配置优先使用
* 细节
* 1、命名空间（配置隔离）
*   默认：public（保留空间）
*       1）开发，测试，生产都有其命名空间
*       2）可以基于环境隔离也可以基于微服务隔离
* 2、配置集
*   一组相关和不想关配置的集合
* 3、配置集ID
*   dataID 类似文件名
* 4、配置分组
*
* 每个微服务创建自己的命名空间，不同生产环境的分组不同
*
* 加载多个配置集
*   配置中心没有的配置才会读取本地的application.properties
* */
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
