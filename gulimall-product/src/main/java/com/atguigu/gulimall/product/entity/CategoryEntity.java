package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * ??Ʒ???????
 * 
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-25 22:35:44
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ????id
	 */
	@TableId
	private Long catId;
	/**
	 * ???????
	 */
	private String name;
	/**
	 * ??????id
	 */
	private Long parentCid;
	/**
	 * ?㼶
	 */
	private Integer catLevel;
	/**
	 * 是否显示0不显示1显示
	 * 当显示字段与全局不同时，需要配置value
	 */
	@TableLogic(value = "1", delval = "0")
	private Integer showStatus;
	/**
	 * ???
	 */
	private Integer sort;
	/**
	 * ͼ????ַ
	 */
	private String icon;
	/**
	 * ??????λ
	 */
	private String productUnit;
	/**
	 * ??Ʒ????
	 */
	private Integer productCount;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<CategoryEntity> children;
}
