package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @description: 购物车商品Vo
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:24
 **/
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    //TODO 库存

    private BigDecimal weight;
}
