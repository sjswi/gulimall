package com.atguigu.gulimall.seckill.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 13:23
 **/
@Controller
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTo> seckillSkuRedisTos = seckillService.getCurrentSeckillSkus();
        System.out.println(seckillSkuRedisTos);
        return R.ok().setData(seckillSkuRedisTos);
    }
    @ResponseBody
    @PostMapping("/getSkuSeckillInfo")
    public R getSkuSeckillInfo(@RequestBody Long skuId){
        SeckillSkuRedisTo redisTo = seckillService.getSkuSeckillInfo(skuId);
        System.out.println(redisTo);

        return R.ok().setData(redisTo);
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId,@RequestParam("key") String key,@RequestParam("num") Integer num, Model model){
        System.out.println(killId);
        System.out.println(key);
        System.out.println(num);
        String kill = seckillService.kill(killId, key, num);

        model.addAttribute("orderSn", kill);

        return "success";
    }
}
