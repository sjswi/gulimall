package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.util.ObjectBuilder;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModelTo;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.ProductSaveService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @description: service的实现
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-08 17:42
 **/
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private ElasticsearchClient client;
    @Override
    public boolean productStatusUp(List<SkuEsModelTo> skuEsModelToList) throws IOException {
        //保存到es
//        1、给es中建立索引，并建立好映射关系

//        2、给es中保存数据
        BulkRequest.Builder builder = new BulkRequest.Builder();
        List<BulkOperation> operations = new ArrayList<>();
        for (SkuEsModelTo s : skuEsModelToList) {
            BulkOperation.Builder builder1 = new BulkOperation.Builder();
            builder1.index(s1 -> s1.index(EsConstant.PRODUCT_INDEX).id(s.getSkuId().toString()).document(s));
            operations.add(builder1.build());
        }
        builder.operations(operations);
        BulkRequest bulkRequest = builder.build();

        BulkResponse bulk = client.bulk(bulkRequest);
        //TODO 如果批量错误
        if (bulk.errors()) {
            List<String> collect = bulk.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());
            log.error("商品上架错误:{}", collect);
//            for (BulkResponseItem item : bulk.items()) {
//                if(item.error()!=null) {
//                    System.out.println(item.error().metadata().toString());
//                    System.out.println(item.error().reason());
//                    System.out.println(item.error().rootCause());
//                    System.out.println(item.error().causedBy().reason());
//                }
//            }
        }
        return bulk.errors();

    }
}

