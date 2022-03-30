package com.atguigu.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @description: sku在es中保存的模型数据
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 15:16
 **/
@Data
public class SkuEsModelTo {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private String brandName;//冗余存储字段
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandImg;
    private String CatalogName;
    private List<Attrs> attrs;
    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}

