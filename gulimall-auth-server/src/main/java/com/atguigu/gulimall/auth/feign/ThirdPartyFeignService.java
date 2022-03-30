package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: gulimall
 * @description: 调用第三方服务
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-16 19:49
 **/
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
