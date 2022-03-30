package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.inteceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkus;
import com.atguigu.gulimall.seckill.vo.SeckillSkuVo;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mysql.cj.util.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 12:01
 **/
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    private static final String SECKILL_CHARE_PREFIX = "seckill:skus";

    private static final String SKU_STOCK_SEMAPHORE = "seckill:stock:";
    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void uploadSeckillSkuLatest3Days(){

        R r = couponFeignService.getLatest3DaysSession();
        if (r.getCode()==0){
            List<SeckillSessionWithSkus> data = r.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
            });
            saveSessionInfo(data);

            saveSessionSkuInfo(data);
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX+"*");
        for(String key:keys){
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] split = replace.split("_");
            long start = Long.parseLong(split[0]);
            long end = Long.parseLong(split[1]);
            if(time>=start&&time<=end){
                //获得当前场次的所有商品
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
//                System.out.println(range);
                BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
                List<String> objects = boundHashOps.multiGet(range);
//                System.out.println(objects);
                if(objects!=null&&objects.size()>0){
                    List<SeckillSkuRedisTo> collect = objects.stream().map(item -> {
                        SeckillSkuRedisTo redisTo = JSON.parseObject(item, SeckillSkuRedisTo.class);
                        //秒杀开始才可以返回随机码
//                        System.out.println(redisTo);
                        return redisTo;
                    }).collect(Collectors.toList());
//                    System.out.println(collect);
                    return collect;
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        Set<String> keys = hashOps.keys();

        if(keys!=null&&keys.size()>0){
            for(String key:keys) {
                String regx = "\\d_" + skuId;
                if (Pattern.matches(regx, key)){
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    long time = new Date().getTime();
                    assert redisTo != null;
                    if(time>=redisTo.getStartTime()&&time<=redisTo.getEndTime()){
                        System.out.println(redisTo.getRandomCode());
                    }else{
                        redisTo.setRandomCode(null);
                    }
                    return redisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        //首先校验合法性
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String json = hashOps.get(killId);
        System.out.println(json);
        if(!StringUtils.isNullOrEmpty(json)){
            SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();
            long ttl = endTime-startTime;
            //验证时间
            if(time>=startTime&&time<=endTime){
                //验证随机码
                String randomCode = redisTo.getRandomCode();
                if(!StringUtils.isNullOrEmpty(key)&&randomCode.equals(key)){
                    //验证购物数量
                    if(num<=redisTo.getSeckillCount()){
                        //验证是否购买过，幂等性，只要秒杀成功就使用一个key占位
                        String redisKey = memberResponseVo.getId()+"_"+ redisTo.getSkuId();
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if(aBoolean){
                            //占位成功，校验成功
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            try {
                                semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                                //秒杀成功，快速下单，发送消息到消息中间件
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo order = new SeckillOrderTo();
                                order.setOrderSn(timeId);
                                order.setMemberId(memberResponseVo.getId());
                                order.setNum(num);
                                order.setPromotionSessionId(redisTo.getPromotionSessionId());
                                order.setSkuId(redisTo.getSkuId());
                                order.setSeckillPrice(redisTo.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order", order);
                                return timeId;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }

    private void saveSessionInfo(List<SeckillSessionWithSkus> sessions){
        sessions.stream().forEach(session->{
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key =  SESSION_CACHE_PREFIX+startTime+"_"+endTime;

            //判断Redis中是否有该信息，如果没有才进行添加
            Boolean hasKey = redisTemplate.hasKey(key);
            //缓存活动信息
            if (!hasKey) {
                //获取到活动中所有商品的skuId
                List<String> skuIds = session.getRelationEntityList().stream()
                        .map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,skuIds);
            }

        });
    }

    private void saveSessionSkuInfo(List<SeckillSessionWithSkus> sessions){
        sessions.stream().forEach(session -> {
            //准备hash操作，绑定hash
            BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            session.getRelationEntityList().stream().forEach(seckillSkuVo -> {
                System.out.println(seckillSkuVo);
                //生成随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                //redis的key为：场次_商品id
                String redisKey = seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString();
                if (!operations.hasKey(redisKey)) {

                    //缓存我们商品信息
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    Long skuId = seckillSkuVo.getSkuId();
                    //1、先查询sku的基本信息，调用远程服务
                    R info = productFeignService.getSkuInfo(skuId);
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData(new TypeReference<SkuInfoVo>(){});

                        redisTo.setSkuInfo(skuInfo);
                    }

                    //2、sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);

                    //3、设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    //4、设置商品的随机码（防止恶意攻击）
                    redisTo.setRandomCode(token);

                    //序列化json格式存入Redis中
                    String seckillValue = JSON.toJSONString(redisTo);
                    operations.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(),seckillValue);

                    //如果当前这个场次的商品库存信息已经上架就不需要上架
                    //5、使用库存作为分布式Redisson信号量（限流）
                    // 使用库存作为分布式信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            });
        });
    }
}
