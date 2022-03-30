package com.atguigu.common.constant;

/**
 * @program: gulimall
 * @description: 购物车临时用户的常量，存储在浏览器的cookie中
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:56
 **/
public class CartConstant {
    public static final String TEMP_USER_COOKIE_NAME="user-key";
    public static final int TEMPUSER_COOKIE_TIMEOUT = 60*60*24*30;
    public final static String CART_PREFIX = "gulimall:cart:";
}
