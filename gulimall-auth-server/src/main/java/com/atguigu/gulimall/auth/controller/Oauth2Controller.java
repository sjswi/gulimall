package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.common.vo.SocialUser;
import com.atguigu.gulimall.auth.feign.MemberFeignService;



import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 * @author yaoxinjia
 */
@Slf4j
@Controller
public class Oauth2Controller {
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        String url = "https://api.weibo.com/oauth2/access_token?client_id=1411893798&client_secret=6b03671f1d5bd30edcd63f029a38a428&grant_type=authorization_code&redirect_uri=http://auth.gulimall.com/oauth2.0/weibo/success&code=" +code;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = httpClient.execute(httpPost);
        //2、处理
        if (response.getStatusLine().getStatusCode()==200){
            //获取到accessToken
            String json = EntityUtils.toString(response.getEntity());
            System.out.println(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            //知道当前是哪个社交用户登录成功
            //1、当前用户如果是第一次进网站，就自动注册进来（为当前社交用户生成一个会员信息账号,以后这个社交账号就对应指定的会员）
            //登录或者注册这个社交用户
//            System.out.println(socialUser.toString());
            R r = memberFeignService.oauth2Login(socialUser);
            if (r.getCode()==0){
                MemberResponseVo memberRespVo = r.getData(new TypeReference<MemberResponseVo>() {});
                System.out.println("登录成功，用户信息：" + memberRespVo);
                log.info("登录成功，用户信息：" + memberRespVo);
                //TODO 1、默认发的令牌 session=dadas,作用域只是当前域，（解决子域与父域session共享问题）
                //TODO 2、使用json的序列化方式来序列化对象数据到redis中
//                第一次使用session，命令浏览器保存卡号，即JessionId这个cookie,
//                子域：gulimall.com,，auth.gulimall.com,指定域名为父域名，即使是子域系统的即JessionId，父域名也能使用
//                由于session的服务范围（域名）是服务器自己默认设置的,因此需要其他方法解决子域名与父域名的sessionId共享问题
//                session是存储在服务器，sessionId存储在客户端，

                session.setAttribute(AuthServerConstant.SESSION_LOGIN_KEY,memberRespVo);
                //2、登录成功就跳回首页
                return "redirect:http://gulimall.com";
            }else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else{
            return "redirect:http://gulimall.com/login.html";
        }

    }
}
