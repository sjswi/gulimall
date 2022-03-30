package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.feign.SeckillFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SeckillSkuVo;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SeckillFeignService seckillFeignService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skus) {
        this.baseMapper.insert(skus);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        /**
         * key
         * catalog_id
         * brand_id
         * min
         * max
         * **/
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
//            此处使用and是为了限定其中的模糊查询在内部
            wrapper.and((w)->{
                w.eq("sku_id", key).or().like("sku_name",key);
            });
        }
        String catelog_id = (String) params.get("catelog_id");
        if(!StringUtils.isNullOrEmpty(catelog_id)&&!"0".equalsIgnoreCase(catelog_id)){

            wrapper.eq("catalog", catelog_id);
        }
        String brand_id = (String) params.get("brand_id");
        if(!StringUtils.isNullOrEmpty(brand_id)&&!"0".equalsIgnoreCase(brand_id)){
            wrapper.eq("brand_id", brand_id);
        }
//        最小最大价格是一个价格区间ge大于等于,le小于等于
        String min = (String) params.get("min");
        if(!StringUtils.isNullOrEmpty(min)){
            wrapper.ge("price", min);
        }
        String max = (String) params.get("max");

        if(!StringUtils.isNullOrEmpty(max)){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);
                int i = bigDecimal.compareTo(new BigDecimal(0));
                if(i==1){
                    wrapper.le("price", max);
                }
            }catch (Exception e){

            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> spuId1 = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return spuId1;
    }

    @Override
    public SkuItemVo item(Long skuId){
        SkuItemVo skuItemVo = new SkuItemVo();
        //        异步查询所有信息
        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //        sku基本信息
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> future = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            //        spu销售信息
            List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }, threadPoolExecutor);
        CompletableFuture<Void> future1 = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            //        spu的介绍

            SpuInfoDescEntity byId = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(byId);
        }, threadPoolExecutor);
        CompletableFuture<Void> voidCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            //        获取规格参数信息
            List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());

            skuItemVo.setGroupAttrs(attrGroupWithAttrsBySpuId);
        }, threadPoolExecutor);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            //        sku图片信息获取
            List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesById(skuId);
            skuItemVo.setImages(imagesEntities);
        }, threadPoolExecutor);

        //查询当前商品是否参加秒杀活动
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            R r = seckillFeignService.getSkuSeckillInfo(skuId);
            if(r.getCode()==0){
                SeckillSkuVo data = r.getData(new TypeReference<SeckillSkuVo>() {
                });

                skuItemVo.setSeckillSkuVo(data);
            }
        }, threadPoolExecutor);

//        等待所有任务都完成
        try {
            CompletableFuture.allOf(future, future2, future2, voidCompletableFuture,future3).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return skuItemVo;
    }

}