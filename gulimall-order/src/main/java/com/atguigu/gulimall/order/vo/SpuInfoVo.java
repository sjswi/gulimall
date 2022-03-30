package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-26 10:11
 **/
@Data
public class SpuInfoVo {
    private Long id;
    /**
     * ??Ʒ???
     */
    private String spuName;
    /**
     * ??Ʒ????
     */
    private String spuDescription;
    /**
     * ????????id
     */
    private Long catalogId;
    /**
     * Ʒ??id
     */
    private Long brandId;
    /**
     *
     */
    private BigDecimal weight;
    /**
     * ?ϼ?״̬[0 - ?¼ܣ?1 - ?ϼ?]
     */
    private Integer publishStatus;

    private String brandName;
}
