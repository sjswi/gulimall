package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: gulimall
 * @description: 页面详情信息
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-15 12:57
 **/
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * @params: Long
     * @return: String
     * @author: yuxiaobing
     * @date: 2022/3/15
     **/
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId, Model model){
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        System.out.println("准备查询"+skuId);
        model.addAttribute("item", skuItemVo);

//        System.out.println(skuItemVo.getSeckillSkuVo().getRandomCode());
        return "shangpinxiangqing";
    }
}
