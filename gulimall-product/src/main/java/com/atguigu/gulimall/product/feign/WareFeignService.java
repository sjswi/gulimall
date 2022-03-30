package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @program: gulimall
 * @description: 远程调用Ware包的内容
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 17:09
 *
 *
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {
    /**
     * @params: List<Long>
     * @return: R
     * @author: yuxiaobing
     * @date: 2022/3/8
     * 1、R设计是加上范型
     * 2、直接返回我们需要的结果
     * 3、自己封装解析结果
     **/
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
