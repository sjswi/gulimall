package com.atguigu.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.StockDetailTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.to.OrderTo;
import com.atguigu.gulimall.ware.vo.OrderVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-26 19:43
 **/
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 处理StockLockedTo库存解锁
     * @params: StockLockedTo Message Channel
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/28
     **/
    @RabbitHandler
    public void handlerStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println(to);
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }



    }

    /**
     * 处理OrderTo库存解锁
     * @params: OrderTo Message Channel
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/28
     **/
    @RabbitHandler
    public void handlerStockLockedRelease(OrderTo to, Message message, Channel channel) throws IOException {
        System.out.println(to);
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }



    }
}
