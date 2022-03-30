package com.atguigu.common.to;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 库存锁定成功的To
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-26 18:31
 **/
@Data
public class StockLockedTo implements Serializable {

    private Long id;//库存工作单的id

    private StockDetailTo detailTo;//工作单详情的Id


}
