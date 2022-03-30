package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: gulimall
 * @description: 提交订单
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-25 22:50
 **/
@Data
public class OrderSubmitVo {
    private Long addrId;

    private Integer payType;

    //无需提交商品，直接向购物车再请求一次即可

    private String orderToken;

    private BigDecimal payPrice;//应付价格
}
