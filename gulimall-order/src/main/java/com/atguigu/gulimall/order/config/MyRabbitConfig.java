package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: gulimall
 * @description: rabbitmq的配置文件
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 13:35
 **/
@Configuration
public class MyRabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    public void

    /**
     * 三种确认，1、publisher->broker(exchange),2、exchange->queue,3、queue->consumer
     *
     * 1、设置消息确认回调ConfirmCallback():publisher->broker
     * 配置文件中需要配置
     *  publisher-confirm-type:
     * 2、queue消息确认回调ReturnsCallback():exchange->queue
     * 3、consumer确认（保证每个消息都被消费，消费后broker才可以删除消息）
     *  3.1默认自动确认，消息发送给消费者，broker自动确认，直接删除消息
     *     问题：如果消息传输过程中丢失，就会导致消息丢失
     *  3.2手动确认，需要配置listener
     *     只要没有明确告诉rabbitmq消息被确认，消息就会一直处于unacked状态，连接断开后，消息会重新变成ready状态
     *     使用channel.basicAck()来进行确认
     *
     * @params:
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/24
     **/
    @PostConstruct
    public void initRabbitTemplate(){
        //消息没有投递到交换机（exchange）就会失败，
        rabbitTemplate.setConfirmCallback((correlationData, b, s) -> {

        });
        //exchange投递到对应的队列，只要没有投递到所有的队列就会失败，失败就会调用该函数
        rabbitTemplate.setReturnsCallback(returnedMessage -> {

        });
    }
    //rabbitMq创建队列
    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000L);
        return new Queue("order.delay.queue", true, false, false, arguments);

    }
    /**
     * 容器中的Bingding，Queue，Exchange都会自动创建（rabbitMq没有的情况下）
     * @params:
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/26
     **/
    @Bean
    public Queue orderReleaseQueue(){
        return new Queue("order.release.order.queue", true, false, false);
    }
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange",true, false);
    }
    @Bean
    public Binding orderCreateOrderBingding(){
        return new Binding("order.delay.queue"
                , Binding.DestinationType.QUEUE
                , "order-event-exchange"
                ,"order.create.order"
                ,null);
    }
    @Bean
    public Binding orderReleaseOrderBingding(){
        return new Binding("order.release.order.queue"
                , Binding.DestinationType.QUEUE
                , "order-event-exchange"
                ,"order.release.order"
                ,null);
    }
    @Bean
    public Queue orderSeckillOrderQueue(){
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    @Bean
    public Binding orderSeckillOrderBingding(){
        return new Binding("order.seckill.order.queue"
                , Binding.DestinationType.QUEUE
                , "order-event-exchange"
                ,"order.seckill.order"
                ,null);
    }

}
