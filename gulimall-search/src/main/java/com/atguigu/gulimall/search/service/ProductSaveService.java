package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModelTo;

import java.io.IOException;
import java.util.List;

/**
 * @program: gulimall
 * @description: 操作elatsicsearch
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 17:39
 **/

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModelTo> skuEsModelToList) throws IOException;
}
