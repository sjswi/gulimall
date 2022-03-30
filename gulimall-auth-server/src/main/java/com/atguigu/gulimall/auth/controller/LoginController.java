package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberLoginVo;
import com.atguigu.common.vo.MemberRegister;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartyFeignService;

import com.atguigu.gulimall.auth.vo.UserRegister;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @description: 注册的controller
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-16 17:51
 **/
@Controller
public class LoginController {
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeignService memberFeignService;
    /**
     * @params:
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/16
     * 发送一个请求直接跳转一个页面（太麻烦）
     * SpringMVC有一个视图控制器，无需自定义写controller
     **/
//    @GetMapping({"/", "/login.html"})
//    public String login(){
//        return "login";
//    }
//    @GetMapping("/register.html")
//    public String register(){
//        return "register";
//    }
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendcode(@RequestParam("phone") String phone){
        //查询redis看该手机号是否发送过验证码，如果已经发送则返回一些错误信息
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isNullOrEmpty(redisCode)) {

            long s = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - s < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

//        验证码的再次校验,key：phone，value：code
        System.out.println(phone);
        String code = UUID.randomUUID().toString().substring(0, 4);
        String substring = code+"_"+System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone, substring, 10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }
    /**
     * 具体的注册业务
     * @params: UserRegister,BindingResult,RedirectAttributes
     * @return: String
     * @author: yuxiaobing
     * @date: 2022/3/16
     **/
    @PostMapping("/regist")
    public String regist(@Valid UserRegister vo, BindingResult result, RedirectAttributes redirectAttributes){
//        注册成功
        System.out.println(vo.toString());
        if(result.hasErrors()){
//            校验出错
            Map<String, String> errors = new HashMap<>();
            errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
//            model.addAttribute("errors", errors);
//            使用转发会出现post not supported
//            使用直接页面解析器到register会重新出现协议信息
//            重定向无法携带数据到前端
//            RedirectAttributes模拟重定向携带书
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/register.html";
//            重定向需要完全的域名网页，不然会暴露服务器ip端口信息
//            但是完全域名依旧无法携带信息
//            因此需要使用session携带信息


//

        }
//        注册
//            1、校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if(!StringUtils.isNullOrEmpty(s)){
            String s1 = s.split("_")[0];
            if(s1.equals(code)){
//                    验证码正确
//                必须删除验证码，令牌机制，旧的成功了就删除，因此每个验证码只能使用一次
                redisTemplate.opsForValue().getAndDelete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                MemberRegister m = new MemberRegister();
                BeanUtils.copyProperties(vo,m);

                R regist = memberFeignService.regist(m);
                if(regist.getCode()==0){
//                    注册成功
                    return "redirect:http://auth.gulimall.com/login.html";
                }else{
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", regist.getData(new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/register.html";
                }
            }else{
                Map<String, String> errors = new HashMap<>();
                errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));

                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/register.html";
            }
        }else{
            Map<String, String> errors = new HashMap<>();
            errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));

            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/register.html";
        }
//        return "redirect:/login.html";
    }
    @GetMapping("/lgin.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.SESSION_LOGIN_KEY);
        if(attribute!=null){
            return "login";
        }else{
            return "redirect:http//auth.gulimall.com/register.html";
        }

    }
    @PostMapping("/login")
    public String login(MemberLoginVo vo, RedirectAttributes redirectAttributes, HttpSession httpSession){
        R r = memberFeignService.login(vo);
        if(r.getCode()==0){
            httpSession.setAttribute(AuthServerConstant.SESSION_LOGIN_KEY, r.getData(new TypeReference<MemberResponseVo>(){}));

            return "redirect:http://gulimall.com";
        }else{
            Map<String, String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            System.out.println(errors);
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }
}
