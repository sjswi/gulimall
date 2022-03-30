package com.atguigu.gulimall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @program: gulimall
 * @description: 目标用户信息，判断用户是否登陆。使用购物车的用户是登陆用户还是离线用户
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:52
 **/
@Data
@ToString
public class UserInfoTo {
    private Long userId;

    private String userKey;

    private Boolean tempUser=false;
}
