package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.SkuHasStockTo;

import com.atguigu.common.to.StockLockedTo;
import com.atguigu.gulimall.ware.to.OrderTo;
import com.atguigu.gulimall.ware.vo.LockStockResult;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-26 10:41:32
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds);


    boolean orderLockStock(WareSkuLockVo wareSkuLockVo);

    void unlockStock(StockLockedTo to);

    @Transactional(rollbackFor = Exception.class)
    void unlockStock(OrderTo orderTo);

}

