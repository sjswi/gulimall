package com.atguigu.gulimall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 12:26
 *
 **/
@Data
public class SeckillSkuVo {
    private Long id;
    /**
     * ?id
     */
    private Long promotionId;
    /**
     * ?????id
     */
    private Long promotionSessionId;
    /**
     * ??Ʒid
     */
    private Long skuId;
    /**
     * ??ɱ?۸
     */
    private BigDecimal seckillPrice;
    /**
     * ??ɱ????
     */
    private Integer seckillCount;
    /**
     * ÿ???޹?????
     */
    private Integer seckillLimit;
    /**
     * ???
     */
    private Integer seckillSort;
}
