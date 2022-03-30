package com.atguigu.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-29 11:43
 **/
//@Slf4j
//@EnableAsync
//@Component
//@EnableScheduling
public class HelloSchedule {

    /**
     * springBoot中cron表达式只能6位，不能出现年
     * 且周的位置为1-7分别代表周一到周日
     * cron表达式a/b，从a开始每b执行一次
     * 定时任务不应该阻塞，默认是阻塞的
     * 解决方法：1、任务放在线程池，异步执行
     *         2、 定时任务线程池,默认定时任务的线程池的线程个数为1（效果一般）
     *         3、异步任务 @EnableAsync，需要异步执行的方法使用@Async注解 自动配置类 @TaskExectorAutoConfiguration
     * 使用异步加定时任务实现定时任务不阻塞
     * @params:
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/29
     **/
//    @Async
//    @Scheduled(cron = "* * * * * 2")
//    public void hello() throws InterruptedException {
//        log.info("hello");
//        Thread.sleep(3000);
//    }
}
