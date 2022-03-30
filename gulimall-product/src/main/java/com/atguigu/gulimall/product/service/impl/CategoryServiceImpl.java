package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.mysql.cj.util.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
//    @Autowired
//    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithtree() {
//        1、查出所有的分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//        2、组装成树结构
//          2.1找到所有的一级分类
        List<CategoryEntity> collect = categoryEntities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0).map((memu) -> {
            memu.setChildren(getChildren(memu, categoryEntities));
            return memu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
//          2.2
        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
//TODO 1、检查当前删除的菜单，是否被其他地方引用
//        当前开发一般使用逻辑删除，使用某个字段代表是否被删除
        baseMapper.deleteBatchIds(asList);
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> collect = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
//            找到所有子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
//            菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return collect;
    }

    /*
     * 找到catelog完整id
     * */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        CategoryEntity byId = this.getById(catelogId);
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return paths.toArray(new Long[0]);
    }

    /**
     * 级联更新，更新所有关联的数据
     * @CacheEvict 失效模式（解决缓存与数据的一致性问题）
     * 1、同时进行多种缓存操作，
     * 2、指定删除某个分区下的所有数据@CacheEvict(allentry=true)
     * 3、存储同一类型的数据可以指定分区。默认分区名就是前缀
     * @CachePut 双写模式
     **/
//    @Cacheable(value={"category23"},key="'getLevel1Categories'") //数据的值有修改就会删除redis的缓存，常规字符串需要加单引号
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

//    @Cacheable(value="category")
    @Cacheable(value="category", key = "'category:'+#root.methodName")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
        return categoryEntities;
    }

    @Cacheable(value="category", key="'category:'+#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> categoryEntities = getParent_cid(selectList, 0L);
        Map<String, List<Catelog2Vo>> collect2 = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            获得每一个一级分类的二级分类
            List<CategoryEntity> categoryEntities1 = getParent_cid(selectList, v.getCatId());
//            封装上面的结果
            List<Catelog2Vo> collect = null;
            if (categoryEntities1 != null) {
                collect = categoryEntities1.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    //二级分类对应的三级分类
                    List<CategoryEntity> categoryEntities2 = getParent_cid(selectList, item.getCatId());
                    if (categoryEntities2 != null) {
                        List<Catelog2Vo.Catelog3Vo> collect1 = categoryEntities2.stream().map(item_3 -> {
//                            格式封装
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), item_3.getCatId().toString(), item_3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect1);
                    }
                    return catelog2Vo;

                }).collect(Collectors.toList());
            }
            return collect;
        }));
        return collect2;

    }

    //    springboot2以后默认使用lettuce作为操作redis的客户端，lettuce使用netty进行网络通信
//    lettuce的bug导致netty堆外内存溢出
//    可以通过-Dio.netty.maxDirectMemory去调整直接内存的大小
//    解决方法，不能直接调整直接内存的大小
//    1、升级lettuce客户端
//    2、切换到jedis
//    lettuce,jedis都是底层操作redis的客户端，springboot使用redistemplate对其进行了再次封装


    /**
     * 不实用springCache直接使用redisTemplate操作redis
     * @params: null
     * @return: Map<String, List<Catelog2Vo>>
     * @author: yuxiaobing
     * @date: 2022/3/11
     **/

//    public Map<String, List<Catelog2Vo>> getCatalogJsonRedis() {
////        1、空结果缓存，解决缓存穿透，（布隆过滤器）
////        2、设置随机过期时间（设置值+随机值）解决缓存雪崩
////        3、加锁：解决缓存击穿（多个请求同一个数据直接响应到数据库）
//        /**
//         * 同一把锁就能锁住需要这个锁的全部线程，
//         * 加锁方式(非分布式)
//         * 1、synchronized(this)， SpringBoot的所有组件在容器（非分布式下一个项目一个容器）中都是单例的
//         * 2、synchronized 加到方法块中，为了避免排队连续查数据库，得到锁以后，
//         * 应该再去缓存中确定一次，如果缓存中有，不应该继续查数据库
//         * 分布式下需要分布式锁
//         * TODO 本地锁：synchronized，JUC（lock），在分布式情况下，想要锁住所有必须使用分布式锁
//         * 分布式锁，redis set key nx 占坑(RedisTemplate.setIfAbsent("lock"，"11"))
//         * 加锁成功执行业务然后删除坑位，加锁失败重试（自旋）(问题，如果成功拿到锁的执行失败，线程终止没法删除锁会导致死锁)
//         * 解决思索，设置锁的过期时间（加锁跟设置过期时间需要原子操作）
//         * 删除锁也会出现问题（当业务执行时间过长，锁自动过期，而删除锁很有可能删除其他线程的锁），
//         * 解决方法（先获得锁的内容，保证该锁是自己加上的）
//         * 这种解决方法由于不是原子操作，还是有问题（即，获得锁内容后到删除锁的时间内，该锁过期，其他线程抢到锁，然后删除了其他线程的锁）
//         * 解决上述问题，可以使用原子操作(将删除锁的操作设置成原子操作)具体操作，设置脚本redisTemplate.execte(new DefaultRedisScript<Integer>("if reids.call('get',KEYS[1])==ARGV[1] then return redis.call('del', KEYS[1]) else return 0"),Integer.class)
//         * 为了避免锁过期，可以进行锁续期
//         * **/
////        加入缓存逻辑，缓存中存的是json字符串
////        从缓存中获取的数据需要转换成我们需要的对象类型，即序列化和反序列化过程
//        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
//        if (StringUtils.isNullOrEmpty(catalogJson)) {
////            缓存中没有，查询数据库
//            Map<String, List<Catelog2Vo>> catalogJsonFromDb = this.getCatalogJsonFromDbRedissonLock();
////            查到的数据放入缓存，将对象转为json放在缓存中
//            String s = JSON.toJSONString(catalogJsonFromDb);
//            stringRedisTemplate.opsForValue().set("catalogJson", s);
//            return catalogJsonFromDb;
//
//        }
////        将从缓存得到的json字符串转为对象
//        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//        });
//        return result;
//    }
    //缓存数据一致性问题，
//    双写模式：同时写缓存和数据库，问题（写数据库的时间不一致，多次写时会出现后写请求先写入缓存，从而导致数据不一致）
//    解决方法：加锁，必须让先到的先写，
//    失效模式：写数据库，将缓存删除 问题（假设有三个请求，第一个请求写数据删除缓存，第二个请求写数据删除缓存，但是第二个数据运行较慢，第三个数据读缓存然后查找数据库，查到了第一个请求写入的数据，放入缓存，从而导致数据不一致）
//    解决方法：加锁
//    经常修改的数据不要放入缓存中
//    canal解决缓存数据一致性，canal更新缓存，canal订阅
//    mysql的二进制日志来确定哪些数据进行过修改，从而更新redis
//    canal可以解决数据异构问题
//    1、缓存中的所有数据都有过期时间，数据过期下一次查询触发主动更新，
//    2、读写数据时使用读写锁
    //使用redisson分布式锁
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parent_cid;
        }).collect(Collectors.toList());
        return collect;
        // return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbRedissonLock() {
//    /**
//     * 1、注意点：锁的名字，锁的粒度，越细越快
//     * 锁的粒度：具体缓存的某个数据
//     * */
//        RLock lock = redissonClient.getLock("CatalogJson-lock");
//        lock.lock();
//        try {
//            List<CategoryEntity> selectList = baseMapper.selectList(null);
//            List<CategoryEntity> categoryEntities = getParent_cid(selectList, 0L);
//            Map<String, List<Catelog2Vo>> collect2 = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
////            获得每一个一级分类的二级分类
//                List<CategoryEntity> categoryEntities1 = getParent_cid(selectList, v.getCatId());
////            封装上面的结果
//                List<Catelog2Vo> collect = null;
//                if (categoryEntities1 != null) {
//                    collect = categoryEntities1.stream().map(item -> {
//                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
//                        //二级分类对应的三级分类
//                        List<CategoryEntity> categoryEntities2 = getParent_cid(selectList, item.getCatId());
//                        if (categoryEntities2 != null) {
//                            List<Catelog2Vo.Catelog3Vo> collect1 = categoryEntities2.stream().map(item_3 -> {
////                            格式封装
//                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), item_3.getCatId().toString(), item_3.getName());
//                                return catelog3Vo;
//                            }).collect(Collectors.toList());
//                            catelog2Vo.setCatalog3List(collect1);
//                        }
//                        return catelog2Vo;
//
//                    }).collect(Collectors.toList());
//                }
//                return collect;
//            }));
//            return collect2;
//        }finally {
//            lock.unlock();
//        }
//    }

    private List<Long> findParentPath(Long cateloId, List<Long> path) {
        path.add(cateloId);
        CategoryEntity byId = this.getById(cateloId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), path);
        }
        return path;

    }
}