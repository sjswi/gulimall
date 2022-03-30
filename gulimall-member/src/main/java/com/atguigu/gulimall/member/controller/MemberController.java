package com.atguigu.gulimall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.vo.MemberLoginVo;
import com.atguigu.common.vo.MemberRegister;
import com.atguigu.common.vo.SocialUser;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * ??Ա
 *
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-26 10:31:56
 */

@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;



//    @PostMapping(value = "/register")
//    public R register(@RequestBody MemberUserRegisterVo vo) {
//
//        try {
//            memberService.register(vo);
//        } catch (PhoneException e) {
//            return R.error(BizCodeEnum.PHONE_EXISTS_EXCEPTION.getCode(),BizCodeEnum.PHONE_EXISTS_EXCEPTION.getMsg());
//        } catch (UsernameException e) {
//            return R.error(BizCodeEnum.USER_EXISTS_EXCEPTION.getCode(),BizCodeEnum.USER_EXISTS_EXCEPTION.getMsg());
//        }
//
//        return R.ok();
//    }


//    @PostMapping(value = "/login")
//    public R login(@RequestBody MemberUserLoginVo vo) {
//
//        MemberEntity memberEntity = memberService.login(vo);
//
//        if (memberEntity != null) {
//            return R.ok().setData(memberEntity);
//        } else {
//            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
//        }
//    }


//    @PostMapping(value = "/oauth2/login")
//    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {
//
//        MemberEntity memberEntity = memberService.login(socialUser);
//
//        if (memberEntity != null) {
//            return R.ok().setData(memberEntity);
//        } else {
//            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
//        }
//    }
//
//    @PostMapping(value = "/weixin/login")
//    public R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo) {
//
//        MemberEntity memberEntity = memberService.login(accessTokenInfo);
//        if (memberEntity != null) {
//            return R.ok().setData(memberEntity);
//        } else {
//            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
//        }
//    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        MemberEntity member = memberService.getById(id);
        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegister memberRegister){
        try {
            memberService.regist(memberRegister);
        }catch (PhoneExistException e){
            return R.error(BizCodeEnum.PHONE_EXISTS_EXCEPTION.getCode(),BizCodeEnum.PHONE_EXISTS_EXCEPTION.getMsg());
        }catch (UserNameExistException e){
            return R.error(BizCodeEnum.USER_EXISTS_EXCEPTION.getCode(),BizCodeEnum.USER_EXISTS_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo) {

        MemberEntity memberEntity = memberService.login(memberLoginVo);
        if(memberEntity==null){
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
        }

        return R.ok().setData(memberEntity);
    }
    @PostMapping(value = "/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {

        MemberEntity memberEntity = memberService.login(socialUser);
        System.out.println(memberEntity.toString());
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
        }
    }

//    @PostMapping(value = "/weixin/login")
//    public R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo) {
//
//        MemberEntity memberEntity = memberService.login(accessTokenInfo);
//        if (memberEntity != null) {
//            return R.ok().setData(memberEntity);
//        } else {
//            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
//        }
//    }



}
