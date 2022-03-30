package com.atguigu.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.CreateResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
@Data
class User{
    private String name;
    private int age;
    private String gender;
}
@Data
class Account{
    private int account_number;
    private int balance;
    private String firstname;
    private String lastname;
    private int age;
    private String address;
    private String employer;
    private String email;
    private String city;
    private String gender;
    private String state;
}
@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private ElasticsearchClient client;
    @Test
    void contextLoads() {
    }
    @Test
    public void s(){
        System.out.println(client);
    }
    @Test
    public void indexData() throws IOException {

        User user = new User();
        user.setAge(18);
        user.setName("yu");
        user.setGender("男");
        String s = JSON.toJSONString(user);
        CreateResponse createResponse = client.create(c->c.index("user").type("1").id("23").document(user));

        System.out.println(createResponse.toString());
    }
    @Test
    public void searchData() throws IOException {
        SearchRequest.Builder builder = new SearchRequest.Builder();
//        builder.query(new Query());

        SearchRequest searchRequest = builder.build();
        SearchResponse<Account> search = client.search(
                s -> s
                .query(_1 -> _1
                        .match(_2 -> _2
                                .field("gender")
                                .query("M"))),
                Account.class);

        for(Hit<Account> hit:search.hits().hits()){
            Account sou = hit.source();
            System.out.println(sou.toString());
        }

    }

}
