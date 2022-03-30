package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberLoginVo;
import com.atguigu.common.vo.MemberRegister;
import com.atguigu.common.vo.SocialUser;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: gulimall
 * @description: 调用member的服务进行用户注册
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-18 17:06
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody MemberRegister memberRegister);
    @PostMapping("/member/member/login")
    R login(@RequestBody MemberLoginVo memberLoginVo);
    @PostMapping("/member/member/oauth2/login")
    R oauth2Login(@RequestBody SocialUser vo) throws Exception;
//
//    @PostMapping(value = "/member/member/weixin/login")
//    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}
