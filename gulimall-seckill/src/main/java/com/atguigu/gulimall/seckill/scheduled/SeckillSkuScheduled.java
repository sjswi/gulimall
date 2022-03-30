package com.atguigu.gulimall.seckill.scheduled;

import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品的定时上架
 * 每天晚上的3点：上架最新三天需要参与秒杀活动的商品
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 11:58
 **/
@EnableAsync
@Component
@EnableScheduling
@Slf4j
public class SeckillSkuScheduled {

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;
    private static final String  upload_lock="seckill:upload:load";
    //TODO 幂等性
    @Async
    @Scheduled(cron = "0 * * * * ?")
    public void uploadSeckillSkuLatest3Days(){
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(30, TimeUnit.SECONDS);
        //为保证不重复上架保证幂等性，使用分布式锁
        try {
            log.info("上架");
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}

