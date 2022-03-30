package com.atguigu.gulimall.search.controller;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.es.SkuEsModelTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @program: gulimall
 * @description: 调用elastic
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 17:36
 **/
@Slf4j
@RestController
@RequestMapping("/search")
public class ElatsicSaveController {
    @Autowired
    ProductSaveService productSaveService;
    @PostMapping("/product")
    public R productSatusUp(@RequestBody List<SkuEsModelTo> skuEsModelToList){
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModelToList);
        } catch (IOException e) {
            log.error("ES商品上架错误:{}",e);
        }
        if(b){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        return R.ok();
    }
}
