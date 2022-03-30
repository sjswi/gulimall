package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-05 15:44
 **/
@Data
public class AttrGroupWithAttrsVo {


    /**
     * ????id
     */
    @TableId
    private Long attrGroupId;
    /**
     * ????
     */
    private String attrGroupName;
    /**
     * ???
     */
    private Integer sort;
    /**
     * ????
     */
    private String descript;
    /**
     * ??ͼ?
     */
    private String icon;
    /**
     * ????????id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
