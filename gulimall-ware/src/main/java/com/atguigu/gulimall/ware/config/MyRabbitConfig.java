package com.atguigu.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange",true, false);
    }

    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue", true, false, false);

    }
    @Bean
    public Queue stockDelayQueue(){
        //延时队列的参数
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","stock-event-exchange");//死信路由
        arguments.put("x-dead-letter-routing-key","stock.release");//死信路由键
        arguments.put("x-message-ttl",120000L);
        return new Queue("stock.delay.queue", true, false, false, arguments);

    }

    @Bean
    public Binding stockLockBingding(){
        return new Binding("stock.release.stock.queue"
                , Binding.DestinationType.QUEUE
                , "stock-event-exchange"
                ,"stock.release.#"
                ,null);
    }

    @Bean
    public Binding stockDelayBingding(){
        return new Binding("stock.delay.queue"
                , Binding.DestinationType.QUEUE
                , "stock-event-exchange"
                ,"stock.locked"
                ,null);
    }

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
        /**
         *
         * 消息没有投递到交换机（exchange）就会失败，
         * 1、只要消息抵达Broker就ack=true
         * correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
         * ack：消息是否成功收到
         * cause：失败的原因
         */
        //设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause) -> {
            System.out.println("confirm...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
        });



        /**
         * 只要消息没有投递给指定的队列，就触发这个失败回调
         * message：投递失败的消息详细信息
         * replyCode：回复的状态码
         * replyText：回复的文本内容
         * exchange：当时这个消息发给哪个交换机
         * exchange投递到对应的队列，只要没有投递到所有的队列就会失败，失败就会调用该函数
         * routingKey：当时这个消息用哪个路邮键
         */
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
                System.out.println(returnedMessage.getMessage());
            }
        );
    }
}
