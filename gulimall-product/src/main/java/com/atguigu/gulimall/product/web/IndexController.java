package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: gulimall
 * @description: 主页页面跳转
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-09 12:56
 **/
@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @GetMapping({"/","/index"})
    public String indexPage(Model model){
//        视图解析器会进行拼串， prefix+返回值+suffix （prefix和suffix在ThymeleafProperties定义）
        //TODO 查出所有的一级分类
         List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categories();
         model.addAttribute("categories", categoryEntityList);
        return "index";
    }
    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){

        return categoryService.getCatalogJson();
    }
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
//        锁的时间会自动续期时间是30s，锁的唯一id就是锁的名字
//        加锁的业务只要运行完成，就不会给当前锁续期
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock();//阻塞式等待，如果指定了过期时间，锁不会自动续期，因此必须保证设置的过期时间大于业务执行的时间
        //没有指定过期时间，则使用默认时间30s，LockWatchdogTimeout的默认时间，
//        只要占锁成功，就会启动一个定时任务，重新给锁设置过期时间，新的时间还是LockWatchdogTimeout的默认时间，每隔10s都会续期
//        最佳实战，
//        Lock.lock(30s)
        try{
            System.out.println("加锁成功");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "hello";
    }
//    读写锁可以保证一定能读到最新数据，写锁是排他锁，读锁是共享锁，写锁需等到读锁释放才能加锁成功
    @GetMapping("/write")
    @ResponseBody
    public String write(){
        RReadWriteLock lock = redissonClient.getReadWriteLock("read-write");
        RLock rLock = lock.writeLock();
        String s="";
        try{
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(3000);
            redisTemplate.opsForValue().set("write",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }
    @GetMapping("/read")
    @ResponseBody
    public String read(){

        RReadWriteLock lock = redissonClient.getReadWriteLock("read-write");
        RLock rLock = lock.readLock();
        String s="";
        rLock.lock();
        try{
            Thread.sleep(3000);
            s = redisTemplate.opsForValue().get("write");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }
//    acquire是阻塞的，没有获得就会阻塞，tryacquire不是阻塞的
    @ResponseBody
    @GetMapping("/lockdoor")
    public String lockDoor(){
        return "放假";
    }
    @ResponseBody
    @GetMapping("/hello1")
    public String hello1(){
        return "hello";
    }
}
