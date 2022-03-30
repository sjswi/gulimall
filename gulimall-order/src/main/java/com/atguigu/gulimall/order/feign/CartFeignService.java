package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @program: gulimall
 * @description: 远程调用购物车信息
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:58
 **/
@FeignClient("gulimall-cart")
public interface CartFeignService {
    /**
     * feign的远程调用cookie得不到的问题，
     * 原因：feign远程调用是构造新的request请求，这个新的请求没有cookie信息
     * 解决方法：加上一个requestInteceptor
     * **/
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentCartItems();
}
