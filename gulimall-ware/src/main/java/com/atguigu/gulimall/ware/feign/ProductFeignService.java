package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: gulimall
 * @description: 远程调用product包
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-06 19:34
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * feign有两种
     *  1、让所有请求过网关
     *      @FeignClient("gulimall-gateway")
     *      /api/product/skuinfo/info/{skuId}
     *  2、直接让后台指定服务处理
     *      @FeignClient("gulimall-product")
     *      /product/skuinfo/info/{skuId}
     *
     * **/
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
