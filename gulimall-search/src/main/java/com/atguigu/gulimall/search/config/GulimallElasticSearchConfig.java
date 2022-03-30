package com.atguigu.gulimall.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;

import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;

import static com.alibaba.nacos.api.common.Constants.TOKEN;

/**
 * @program: gulimall
 * @description: elasticsearch的配置
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-07 22:06
 * 1、导入依赖
 * 2、编写配置，给容器中注入一个RestHighLevelClient
 **/
@Configuration
public class GulimallElasticSearchConfig {
//    public static final TransportOptions COMMON_OPTION;
//    static {
//        TransportOptions.Builder builder = new TransportOptions.Builder() {
//            @Override
//            public TransportOptions.Builder addHeader(String s, String s1) {
//                return null;
//            }
//
//            @Override
//            public TransportOptions.Builder setParameter(String s, String s1) {
//                return null;
//            }
//
//            @Override
//            public TransportOptions.Builder onWarnings(Function<List<String>, Boolean> function) {
//                return null;
//            }
//
//            @Override
//            public TransportOptions build() {
//                return null;
//            }
//        };
////        builder.addHeader("Authorization", "Bear"+TOKEN);
////        builder.setHttpAsyncResponseConsumerFactory(
////                new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30*1024*1024*1024));
//        COMMON_OPTION = builder.build();
//    }
    @Bean
    public ElasticsearchClient esRestClient(){
        RestClient restClient = RestClient.builder(
                new HttpHost("101.201.76.21", 9200)).build();

// Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

// And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        return client;
    }
}
