package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-26 10:41:32
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    Long getSkuStock(@Param("skuId") Long sku);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId")Long skuId,@Param("wareId") Long wareId,@Param("num") Integer num);

    void unLockStock(@Param("skuId")Long skuId,@Param("wareId") Long wareId,@Param("num") Integer num);
}
