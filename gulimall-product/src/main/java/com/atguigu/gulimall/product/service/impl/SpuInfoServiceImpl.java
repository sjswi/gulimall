package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.to.es.SkuEsModelTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;

import com.atguigu.gulimall.product.vo.*;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService saveSpuInfoDesc;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {
//      1?????????spu???????????? pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();

        try {
            BeanUtils.copyProperties(spuInfoEntity, spuInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
//      2?????????spu???????????? pms_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        saveSpuInfoDesc.saveSpuInfoDesc(spuInfoDescEntity);
//      3?????????spu????????? pms_spu_images
        List<String> images = spuInfo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
//      4?????????spu???????????? pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        if(baseAttrs==null||baseAttrs.size()==0){
            return;
        }
        List<ProductAttrValueEntity> collect0 = baseAttrs.stream().map(baseAttrs1 -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAttrs1.getAttrId());
            AttrEntity byId = attrService.getById(baseAttrs1.getAttrId());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setAttrValue(baseAttrs1.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttrs1.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect0);

//      5?????????spu?????????sku??????
//        5.1 sku??????????????????pms_sku_info
        List<Skus> skus = spuInfo.getSkus();
        if(skus!=null&&skus.size()>0){
            skus.forEach(item->{
                String defaultImg= "";
                for(Images img:item.getImages()){
                    if(img.getDefaultImg()==1){
                        defaultImg=img.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                try {
                    BeanUtils.copyProperties(skuInfoEntity, item);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                //        5.2 sku??????????????????pms_sku_images

                List<SkuImagesEntity> collect = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    return !StringUtils.isNullOrEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(collect);
//        5.3 sku??????????????????: pms_sku_sale_attrs_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> collect1 = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    try {
                        BeanUtils.copyProperties(skuSaleAttrValueEntity, a);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(collect1);
                //        5.4 sku???????????????????????????gulimall_sms

                Bounds bounds = spuInfo.getBounds();
                SpuBoundTo spuBoundTo = new SpuBoundTo();
                try {
                    BeanUtils.copyProperties(spuBoundTo, bounds);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                spuBoundTo.setSpuId(spuInfoEntity.getId());
                couponFeignService.saveSpuService(spuBoundTo);

                SkuReductionTo skuReductionTo = new SkuReductionTo();
                try {
                    BeanUtils.copyProperties(skuReductionTo, item);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1) {
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0) {
                        log.error("????????????sku??????????????????");
                    }
                }
            });
        }





//      6?????????spu??????????????????gulimall_sms->bound
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * key
         * catalog_id
         * brand_id
         * publish_status
         * **/
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isNullOrEmpty(status)){
            wrapper.eq("publish_status",status);

        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isNullOrEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);

        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isNullOrEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
//        ????????????
        List<SkuEsModelTo> products = new ArrayList<>();
//      ??????spuid?????????sku??????
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
//        ??????spu???????????????????????????????????????

        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrlistforSpu(spuId);
//       ?????????????????????id
        List<Long> collect1 = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> list = attrService.selectSearchAttrsIds(collect1);
        Set<Long> idSet = new HashSet<>(list);
        List<SkuEsModelTo.Attrs> attrsList = productAttrValueEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item->{

            SkuEsModelTo.Attrs attrs1 = new SkuEsModelTo.Attrs();
            try {
                BeanUtils.copyProperties(attrs1, item);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return attrs1;
        }).collect(Collectors.toList());
        //TODO 1???????????????????????????????????????????????????
        Map<Long, Boolean> map = null;
        try {
            List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());


            R r = wareFeignService.getSkuHasStock(skuIdList);
            map = r.getData(new TypeReference<List<SkuHasStockTo>>(){}).stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        }catch (Exception e){
            log.error("?????????????????????????????????{}",e);
        }
        //        ????????????sku?????????
        Map<Long, Boolean> finalMap = map;
        List<SkuEsModelTo> collect = skuInfoEntities.stream().map(item -> {
            //        ???????????????to
            SkuEsModelTo skuEsModelTo = new SkuEsModelTo();
            try {
//                ???????????????????????????????????????set
                BeanUtils.copyProperties(skuEsModelTo, item);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            skuEsModelTo.setSkuPrice(item.getPrice());
            skuEsModelTo.setSkuImg(item.getSkuDefaultImg());
            skuEsModelTo.setSkuTitle(item.getSkuTitle());
           //????????????
            if(finalMap ==null){
                skuEsModelTo.setHasStock(true);
            }else{
                skuEsModelTo.setHasStock(finalMap.get(item.getSkuId()));
            }
            //TODO 2??????????????????0
            skuEsModelTo.setHotScore(0L);
            //TODO 3??????????????????????????????
            BrandEntity byId = brandService.getById(skuEsModelTo.getBrandId());
            skuEsModelTo.setBrandName(byId.getName());
            skuEsModelTo.setBrandImg(byId.getLogo());
            CategoryEntity byId1 = categoryService.getById(skuEsModelTo.getCatalogId());
            skuEsModelTo.setCatalogName(byId1.getName());
//            ??????????????????
            skuEsModelTo.setAttrs(attrsList);
//            ??????sku????????????????????????????????????
            return skuEsModelTo;
        }).collect(Collectors.toList());
//          TODO 5?????????????????????gulimall-search
        R r = searchFeignService.productSatusUp(collect);
        if(r.getCode()==0){
            //??????????????????
            //TODO 6???????????????spu??????

            this.baseMapper.updateSpuStatus(spuId, ProductConstant.SatusEnum.SPU_UP.getCode());
            log.debug("??????????????????");

        }else{
//            ??????????????????
            //TODO 7??????????????????????????????????????????????????????
            /**
             * Feign????????????
             * 1???????????????????????????????????????json
             * 2???????????????????????????
             * 3????????????????????????????????????????????????while(true){try{executeAndDecode}catch{retryer.continue}}
             * **/
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity byId = skuInfoService.getById(skuId);
        return this.baseMapper.selectById(byId.getSpuId());
    }

    private void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}