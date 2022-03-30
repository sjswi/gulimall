package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 锁库存服务
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-26 10:40
 **/
@Data
public class WareSkuLockVo {
    private String orderSn;

    private List<OrderItemVo> locks;
}
