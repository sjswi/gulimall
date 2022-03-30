package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-25 21:33
 **/
@Data
public class FareVo {
    private MemberAddressVo addressVo;
    private BigDecimal fare;
}
