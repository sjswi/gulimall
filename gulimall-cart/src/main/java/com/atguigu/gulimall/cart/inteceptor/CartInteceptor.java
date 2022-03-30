package com.atguigu.gulimall.cart.inteceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import com.mysql.cj.util.StringUtils;
import org.apache.catalina.User;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @program: gulimall
 * @description: 购物车拦截器，判断用户状态。并封装传递给controller
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:49
 **/
@Component
public class CartInteceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    /**
     * 目标方法执行前
     * @params: HttpServletRequest,HttpServletResponse,Object
     * @return: boolean(是否放行)
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(AuthServerConstant.SESSION_LOGIN_KEY);
        UserInfoTo userInfoTo = new UserInfoTo();
        if(attribute!=null){
            userInfoTo.setUserId(attribute.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for(Cookie cookie:cookies){
                String name = cookie.getName();
                if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());

                }
            }
        }
        if(StringUtils.isNullOrEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
            userInfoTo.setTempUser(true);
        }
        threadLocal.set(userInfoTo);
//        目标方法执行之前
        return true;
    }
    /**
     * @params: HttpServletRequest HttpServletResponse Object ModelAndView
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        System.out.println(userInfoTo);
        if(userInfoTo.getTempUser()){
            //持续的延长用户的过期时间
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMPUSER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}
