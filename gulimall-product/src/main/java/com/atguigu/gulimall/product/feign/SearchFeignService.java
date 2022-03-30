package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuEsModelTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @program: gulimall
 * @description: 远程调用ES服务
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 18:41
 **/
@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("/search/product")
    R productSatusUp(@RequestBody List<SkuEsModelTo> skuEsModelToList);
}
