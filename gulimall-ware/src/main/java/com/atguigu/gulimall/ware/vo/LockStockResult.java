package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-26 10:44
 **/
@Data
public class LockStockResult {
    private Long skuId;

    private Integer num;

    private boolean locked;
}
