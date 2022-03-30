package com.atguigu.gulimall.search.thread;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @program: gulimall
 * @description: 线程池测试
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-14 19:13
 **/
public class ThreadTest {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        创建线程的几种方式
//        1、继承Thread
//        2、实现Runnable
//        3、实现Callable
//        4、线程池 优点，可以方便管理线程时间，可以节省开启和销毁线程的开销
        //创建线程池的几种方式
//        1、ThreadPoolExecutor
//        ThreadPoolExecutor pool = new ThreadPoolExecutor(
//                10,
//                100,
//                50,
//                TimeUnit.SECONDS,
//                new SynchronousQueue<>(),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());
//      2、  Executors.newFixedThreadPool()
        System.out.println("main--start");
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程"+Thread.currentThread().getId());
//            int i = 10/0;
//            System.out.println("运行结果:"+i);
//            return i;
//        },executorService).handle((res, thr)->{
//            if(res!=null){
//                return res;
//            }
//            if(thr!=null){
//                return 0;
//            }
//            return 0;
//        });
//        Integer integer = future.get();
//        System.out.println("main--end"+integer);
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程"+Thread.currentThread().getId());
//            int i = 10/0;
//            System.out.println("运行结果:"+i);
//            return i;
//        },executorService).thenCombineAsync(()->{
//
//        },()->{
//
//        });
        test();
    }
    private static void test() {
        System.out.println("开始...");

        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("返回 zhang");
                return "zhang";
            }
        }, executorService).thenCombineAsync(CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("返回 phil");
                return "phil";
            }
        }), new BiFunction<String, String, Object>() {
            @Override
            public Object apply(String s1, String s2) {
                String s = s1 + " , " + s2;
                System.out.println("apply:" + s);
                return s;
            }
        }).whenCompleteAsync(new BiConsumer<Object, Throwable>() {
            @Override
            public void accept(Object o, Throwable throwable) {
                System.out.println("accept:" + o.toString());
            }
        });

        System.out.println("运行至此.");
    }
}
