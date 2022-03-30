package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: gulimall
 * @description: 采购完成数据传送
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-06 18:50
 **/
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;

    private List<PurchaseItemDoneVo> items;
}
