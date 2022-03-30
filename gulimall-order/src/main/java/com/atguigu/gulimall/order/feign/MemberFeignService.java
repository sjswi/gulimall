package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @program: gulimall
 * @description: 查找member的信息
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:34
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {


    @GetMapping("/member/memberreceiveaddress/{memberId}/adresses")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
