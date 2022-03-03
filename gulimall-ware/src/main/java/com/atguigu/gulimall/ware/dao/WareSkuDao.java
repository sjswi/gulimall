package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-26 10:41:32
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
