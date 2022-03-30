package com.atguigu.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 两种存放信息到es的方案
 * 1、存储商品全部信息，优点：方便检索{
 *     skuID
 *     skuTitle
 *     price
 *     saleCount
 *     attrs:{
 *         size
 *         brand
 *         resolution
 *
 *     }
 *
 * }
 * 缺点冗余，许多商品的sku大部分相同
 * 2、存储sku索引{
 *     skuId
 *     spuId
 *
 * }
 * attr索引{
 *
 * }
 * 优点：节省空间
 * 缺点：需要分步查询效率低，原因（大部分的关键字都不只存在一个领域，因此关键字关联了大量的spu和sku）
 * **/
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSearchApplication.class, args);
    }

}
