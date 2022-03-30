package com.atguigu.gulimall.seckill.service;

import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 12:01
 **/
@Service
public interface SeckillService {
    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String  kill(String killId, String key, Integer num);
}
