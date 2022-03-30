package com.atguigu.gulimall.seckill.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 12:25
 **/
@Data
public class SeckillSessionWithSkus {
    private Long id;
    /**
     * ???????
     */
    private String name;
    /**
     * ÿ?տ?ʼʱ?
     */
    private Date startTime;
    /**
     * ÿ?ս???ʱ?
     */
    private Date endTime;
    /**
     * ????״̬
     */
    private Integer status;
    /**
     * ????ʱ?
     */
    private Date createTime;


    @TableField(exist = false)
    private List<SeckillSkuVo> relationEntityList;
}
