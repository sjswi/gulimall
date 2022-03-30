package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-25 23:12
 **/
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code;

}
