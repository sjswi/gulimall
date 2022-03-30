package com.atguigu.common.to;

import lombok.Data;

/**
 * @program: gulimall
 * @description: 远程调用查看是否有库存
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 17:01
 **/
@Data
public class SkuHasStockTo {
    private Long skuId;
    private Boolean hasStock;
}
