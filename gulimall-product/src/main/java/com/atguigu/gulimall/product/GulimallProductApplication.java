package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
* */

@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
