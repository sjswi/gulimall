package com.atguigu.gulimall.member.service;

import com.atguigu.common.vo.MemberLoginVo;
import com.atguigu.common.vo.MemberRegister;
import com.atguigu.common.vo.SocialUser;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.List;
import java.util.Map;

/**
 * ??Ա
 *
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-26 10:31:56
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegister memberRegister);
    void checkPhoneUnique(String phone) throws PhoneExistException;
    void checUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo memberLoginVo);

//    MemberEntity login(String accessTokenInfo);

    MemberEntity login(SocialUser socialUser) throws Exception;



    /**
     * 用户注册
     * @param vo
     */
//    void register(MemberUserRegisterVo vo);
//
//    /**
//     * 判断邮箱是否重复
//     * @param phone
//     * @return
//     */
//    void checkPhoneUnique(String phone) throws PhoneException;
//
//    /**
//     * 判断用户名是否重复
//     * @param userName
//     * @return
//     */
//    void checkUserNameUnique(String userName) throws UsernameException;

//    /**
//     * 用户登录
//     * @param vo
//     * @return
//     */
//    MemberEntity login(MemberUserLoginVo vo);
//
//    /**
//     * 社交用户的登录
//     * @param socialUser
//     * @return
//     */
//    MemberEntity login(SocialUser socialUser) throws Exception;
//
//    /**
//     * 微信登录
//     * @param accessTokenInfo
//     * @return
//     */
//    MemberEntity login(String accessTokenInfo);
}

