package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1、使用rabbitMQ
 *  1.1、引入amqp场景 @RabbitAutoConfiguration会自动生效
 *  1.2、
 *
 *      配置了RabbitTemplate, RabbitConnnectionFactory, RabbitMEssageingTemplate
 *      所有属性在
 *      @ConfigurationProperties(prefix="spring.rabbitmq")
 *  1.3、注解@EnableRabbit
 *  1.4、配置文件配置spring.rabbitmq的配置
 *  1.5、监听消息：@RabbitListener
 *  RabbitMq延时队列:实现1：先把消息放进一个没人消费的队列，等待一定时间后，消息成为死信（没人消费的消息）
 *  然后将死信交给死信路由，然后放进需要可以被消费的队列
 *                  实现2：设置
 *
 *
 *
 * 2、事务失效问题，
 *  同一个对象内事务方法互相调用默认失效，原因绕过了代理对象，事务使用代理对象来控制
 *  解决方法：使用代理对象调用事务方法
 *  2.1、引入spring-boot-starter-aop 引入Aspectj
 *  2.2、@EnableAspectJAutoProxy 开启动态代理功能，以后所有的动态代理都是引入Aspectj，比米阿们对外暴露代理对象
 *  2.3、本类方法互调用
 *3、seata控制分布式事务
 * 3.1、为每个数据库创建一个回滚事务undo_log表
 * 3.2 安装seata服务器
 * 3.3 整合seata
 *      1)导入依赖，spring-cloud-starter-alibaba-seata，
 *      2)registry.conf，修改注册中心到nacos
 *        file.conf
 *      3)所有需要分布式事务的服务都需要配置seata DataSourceProxy代理自己的数据源
 *      4)
 *
 * **/
@EnableAspectJAutoProxy
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
