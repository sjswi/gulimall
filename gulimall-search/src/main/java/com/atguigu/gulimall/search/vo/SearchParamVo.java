package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @description: 跳转search页面搜索
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-11 19:25
 * 封装页面所有可能的查询条件
 **/
@Data
public class SearchParamVo {
    private String keyword; //全文匹配关键字
    private Long catalog3Id;

    /**
     * 排序条件
     * 销量升序和降序排序
     * 热度
     * 价格
     * **/
    private String sort;;//排序条件

    /**
     *
     * 过滤条件
     * 是否有货，价格区间，品牌，类别，属性
     * **/
    private Integer hasStock;

    private String skuPrice;
    private String skuId;
    private String spuId;
    private List<Long> brandIds;
    private Long saleCount;
    private List<String> attrs;
    private String skuTtile;
    private String skuImg;
    private Long pageNum=1L;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private String _queryString;
}
