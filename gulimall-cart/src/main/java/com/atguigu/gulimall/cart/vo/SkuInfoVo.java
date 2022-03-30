package com.atguigu.gulimall.cart.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 15:10
 **/
@Data
public class SkuInfoVo {


    /**
     * skuId
     */

    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * sku???
     */
    private String skuName;
    /**
     * sku????????
     */
    private String skuDesc;
    /**
     * ????????id
     */
    private Long catalogId;
    /**
     * Ʒ??id
     */
    private Long brandId;
    /**
     * Ĭ??ͼƬ
     */
    private String skuDefaultImg;
    /**
     * ???
     */
    private String skuTitle;
    /**
     * ?????
     */
    private String skuSubtitle;
    /**
     * ?۸
     */
    private BigDecimal price;
    /**
     * ????
     */
    private Long saleCount;
}
