package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.vo.MemberLoginVo;
import com.atguigu.common.vo.MemberRegister;
import com.atguigu.common.vo.SocialUser;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
//import com.google.gson.Gson;
import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegister memberRegister) {
        MemberEntity memberEntity = new MemberEntity();
//        设置默认等级
        MemberLevelEntity memberLevel = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevel.getId());
//        设置手机号
//        手机号和用户名应该是唯一的，因此需要重新检查一下
//        为了让controller感知异常，使用异常机制
        this.checkPhoneUnique(memberRegister.getPhone());
        this.checUserNameUnique(memberRegister.getUserName());
//        密码不能明文存储，必须加密
//MD5加密，长度固定，任何不同的内容几乎不可能得到相同的秘文，
        //缺点，秘文固定，因此需要增加随机值，加盐算法
        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = cryptPasswordEncoder.encode(memberRegister.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setUsername(memberRegister.getUserName());
        memberEntity.setMobile(memberRegister.getPhone());
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count>0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checUserNameUnique(String userName) throws UserNameExistException{

        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(count>0) {
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().
                eq("username", memberLoginVo.getLoginacct()).or().
                eq("mobile", memberLoginVo.getLoginacct()));
        if(memberEntity==null){
            return null;

        }else{
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(memberLoginVo.getPassword(), memberEntity.getPassword());
            if(matches){
                return memberEntity;
            }
        }
        return null;
    }


//    @Override
//    public void register(MemberUserRegisterVo vo) {
//
//        MemberEntity memberEntity = new MemberEntity();
//
//        //设置默认等级
//        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
//        memberEntity.setLevelId(levelEntity.getId());
//
//        //设置其它的默认信息
//        //检查用户名和手机号是否唯一。感知异常，异常机制
//        checkPhoneUnique(vo.getPhone());
//        checkUserNameUnique(vo.getUserName());
//
//        memberEntity.setNickname(vo.getUserName());
//        memberEntity.setUsername(vo.getUserName());
//        //密码进行MD5加密
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
//        memberEntity.setPassword(encode);
//        memberEntity.setMobile(vo.getPhone());
//        memberEntity.setGender(0);
//        memberEntity.setCreateTime(new Date());
//
//        //保存数据
//        this.baseMapper.insert(memberEntity);
//    }
//
//    @Override
//    public void checkPhoneUnique(String phone) throws PhoneException {
//
//        Integer phoneCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
//
//        if (phoneCount > 0) {
//            throw new PhoneException();
//        }
//
//    }
//
//    @Override
//    public void checkUserNameUnique(String userName) throws UsernameException {
//
//        Integer usernameCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
//
//        if (usernameCount > 0) {
//            throw new UsernameException();
//        }
//    }
//

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        System.out.println(socialUser.toString());
        //具有登录和注册逻辑
        String uid = socialUser.getUid();

        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
//

        if (memberEntity != null) {

            //这个用户已经注册过
            //更新用户的访问令牌的时间和access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            Map<String,String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender)?1:0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());

                //把用户信息插入到数据库中
                this.baseMapper.insert(register);

            }else{
                log.error("查询当前社交用户的社交账号信息失败");
            }
            return register;
        }

    }

//
//    @Override
//    public MemberEntity login(String accessTokenInfo) {
//
//        //从accessTokenInfo中获取出来两个值 access_token 和 oppenid
//        //把accessTokenInfo字符串转换成map集合，根据map里面中的key取出相对应的value
//        Gson gson = new Gson();
//        HashMap accessMap = gson.fromJson(accessTokenInfo, HashMap.class);
//        String accessToken = (String) accessMap.get("access_token");
//        String openid = (String) accessMap.get("openid");
//
//        //3、拿到access_token 和 oppenid，再去请求微信提供固定的API，获取到扫码人的信息
//        //TODO 查询数据库当前用用户是否曾经使用过微信登录
//
//        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", openid));
//
//        if (memberEntity == null) {
//            System.out.println("新用户注册");
//            //访问微信的资源服务器，获取用户信息
//            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
//                    "?access_token=%s" +
//                    "&openid=%s";
//            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
//            //发送请求
//            String resultUserInfo = null;
//            try {
//                resultUserInfo = HttpClientUtils.get(userInfoUrl);
//                System.out.println("resultUserInfo==========" + resultUserInfo);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            //解析json
//            HashMap userInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
//            String nickName = (String) userInfoMap.get("nickname");      //昵称
//            Double sex = (Double) userInfoMap.get("sex");        //性别
//            String headimgurl = (String) userInfoMap.get("headimgurl");      //微信头像
//
//            //把扫码人的信息添加到数据库中
//            memberEntity = new MemberEntity();
//            memberEntity.setNickname(nickName);
//            memberEntity.setGender(Integer.valueOf(Double.valueOf(sex).intValue()));
//            memberEntity.setHeader(headimgurl);
//            memberEntity.setCreateTime(new Date());
//            memberEntity.setSocialUid(openid);
//            // register.setExpiresIn(socialUser.getExpires_in());
//            this.baseMapper.insert(memberEntity);
//        }
//        return memberEntity;
//    }
//

}