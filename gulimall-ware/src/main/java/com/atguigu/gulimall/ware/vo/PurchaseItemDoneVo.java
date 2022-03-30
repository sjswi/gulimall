package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @description: 采购项完成
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-06 18:51
 **/
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
