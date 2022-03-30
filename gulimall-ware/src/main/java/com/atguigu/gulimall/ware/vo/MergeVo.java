package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @description: 用于合并采购单
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-06 17:38
 **/
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
