package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    SkuSaleAttrValueDao saleAttrValueDao;
    @Test
    public void test(){
//        BrandEntity brandEntity =  new BrandEntity();
//        brandEntity.setName("苹果");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L));
        list.forEach((item)->{
            System.out.println(item);
        });
    }
    @Test
    void contextLoads() {
        System.out.println(redissonClient);
    }

    @Test
    void tests(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(4L,1445L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }
    @Test
    void testSale(){
        List<SkuItemSaleAttrVo> attrGroupWithAttrsBySpuId = saleAttrValueDao.getSaleAttrsBySpuId(4L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }
}
