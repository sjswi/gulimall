package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: gulimall
 * @description: 远程调用coupon的方法
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-05 18:43
 **/
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * @description: CouponFeignService.saveSpuService
     *                  1、@RequestBody将这个对象转为json
     *                  2、找到gulimall-coupon服务，给@PostMapping发送请求，将上一步的json放到请求体位置，发送请求
     *                  3、对方收到请求，请求体有json数据，只要json数据一一对应，无需参数对象一样
     *   是要json数据模型兼容，无需使用同一个to（传输对象Transport object）
     * @params: SpuBoundTo
     * @return: R
     * @author: yuxiaobing
     * @date: 2022/3/5
     **/
    @PostMapping("/coupon/spubounds/save")
    R saveSpuService(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
