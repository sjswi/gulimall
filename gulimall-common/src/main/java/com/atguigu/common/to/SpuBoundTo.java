package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: gulimall
 * @description: 远程调用传递的对象
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-05 18:48
 **/
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
