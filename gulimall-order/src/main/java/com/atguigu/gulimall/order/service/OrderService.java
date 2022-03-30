package com.atguigu.gulimall.order.service;

import com.atguigu.common.to.SeckillOrderTo;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * ????
 *
 * @author yu
 * @email a17281293@gmail.com
 * @date 2022-02-26 10:40:02
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();

    @Transactional(rollbackFor = Exception.class)
    // @GlobalTransactional(rollbackFor = Exception.class)
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderStatusByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    OrderEntity getOrderByOrderSn(String orderSn);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo payAsyncVo);

    void createSeckillOrder(SeckillOrderTo orderTo);
//      微信异步通知
//    String asyncNotify(String notifyData);
}

