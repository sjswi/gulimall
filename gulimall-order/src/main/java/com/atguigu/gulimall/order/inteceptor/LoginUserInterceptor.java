package com.atguigu.gulimall.order.inteceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberResponseVo;
import org.apache.shiro.util.AntPathMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @program: gulimall
 * @description: 拦截器，必须登录后才可以支付
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:06
 **/
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();
        boolean matcher = new AntPathMatcher().match("/order/order/status/**", requestURI);
        boolean match = new AntPathMatcher().match("/payed/notify", requestURI);
        if (matcher||match){
            return true;
        }
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(AuthServerConstant.SESSION_LOGIN_KEY);
        if(attribute!=null){
            //登录了放行
            loginUser.set(attribute);
//            System.out.println(attribute);
           // 应该先取出登录用户，放入
            return true;
        }
        request.getSession().setAttribute("msg", "请先登录");
        response.sendRedirect("http://auth.gulimall.com/login.html");
        return false;
    }
}
