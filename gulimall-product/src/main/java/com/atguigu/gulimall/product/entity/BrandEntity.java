package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.ListValue;
import com.atguigu.common.validator.group.UpdateGroup;
import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * Ʒ?
 * 
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-25 22:35:44
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌ID
	 */
	@Null(message="新增不能指定id", groups = {AddGroup.class})
	@NotNull(message = "修改必须指定ID", groups = {UpdateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * Ʒ??logo??ַ
	 */
	@NotBlank(groups = {AddGroup.class})
	@URL(message="logo必须是一个url地址", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * ???
	 */
	private String descript;
	/**
	 * ??ʾ״̬[0-????ʾ??1-??ʾ]
	 */
	@NotNull(groups = {UpdateStatusGroup.class})
	@ListValue(vals={0,1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * ????????ĸ
	 */
	@NotBlank(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * ???
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0, message = "排序必须大于0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
