package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
/*
* 1.整合mybatis-plus
*   1) 导入依赖
*
*   2）配置
*       1。配置数据源
*           （1）导入数据库驱动
*           （2）配置数据源信息
*       2。配置mybatis-plus
*           （1）使用@MapperScan
*           （2）告诉mybatis-plus的sql文件的位置
* 2.逻辑删除
*   1) 配置全局逻辑删除规则
*   2) 配置逻辑删除的组件bean
*   3) 加上逻辑删除注解@TableLogic
*
* 3.对象存储
*   1、引入oss
* 4.JSR303校验
*   1) 给bean加上校验注解
*   2) 开启校验功能@Valid
*       校验错误会有对应的信息
*   3) 给校验的bean后紧跟一个BindingResult，就可以得到校验的结果
*   4) 分组校验功能
*       1、@NotBlank(message, groups=xxx.class)//groups指定接口
*       2、@Valided指定校验分组
*       3、未指定校验分组的注解在分组校验情况下不生效，
*   5) 自定义校验
*       1、编写自定义的校验注解
*       2、编写自定义的校验器
*       3、关联校验器和校验注解（@Constrain(validatedBy={xxx.class})）
*
* 5、统一的异常处理
* 6、引入模版引擎
*   6.1 thymeleaf关闭缓存
*   6.2 静态资源放在static路径下，可以按照路径访问
*   6.3 页面放在templates文件夹下
*   6.4 页面修改实时更新
*       6.4.1 引入dev-tools
*       6.4.2 重新build项目
*
* 7、redis整合
*   7.1 引入redis-starter-data
*   7.2 简单配置redis的host和port
*   7.3 使用SoringBoot自动配置的StringRedisTemplate来操作redis
*
* 8、整合springCache
*   8.1 引入依赖spring-boot-starter-cache
*   8.2 写配置
*       CacheConfiguration会导入RedisCacheConfiguration，自动配置了缓存管理器，初始化缓存
*       配置使用redis cache.type=redis
*           @Cacheable(cacheName={缓存名})：代表当前的方法结果需要缓存，如果缓存中有，不需要调用方法，直接返回
*           缓存的key：缓存名字（缓存所处的分区）::SimpleKey【
*           缓存的value：默认使用jdk序列化机制，将序列化后的结果存入redis
*           缓存的默认ttl时间：-1（永久不过期）
*       自定义：
*           指定缓存的key，key属性指定，
*           指定缓存的存活时间，配置文件中redis.time_to_live指定，单位毫秒
*           指定数据的保存格式
*   8.3 原理
*       CacheAutoConfiguration -> RedisCacheConfiguration -> 自动配置RedisCacheManager-> 初始化所有缓存->每个缓存的配置
*       ->如果redisCacheCponfiguration中有就用自己写的,没有就使用默认配置->想修改缓存配置，自己编写redisCacheCponfiguration即可->
*       就会应用
*       CacheManager(RedisCacheManage)
*   8.4 不足
*       1) 读模式：
*           缓存穿透:查询null数据，解决空数据：cache-null-values: true
*           缓存雪崩: 大量的key同时过期，解决：加随机时间
*           缓存击穿: 大量并发进来查询同一个数据。解决：加锁 syn=true
*       2) 写模式：
*           读写加锁
*           引入canal,感知mysql的数据更新更新缓存
*           读多写多不存入缓存，直接查询数据库
*       总结：读多写少的常规行数据，即时性一致性要求不高可以使用springCache
*            要求高的数据特殊数据需要特殊数据
*
*
* */

@EnableRedisHttpSession
@EnableConfigurationProperties
@EnableCaching
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
