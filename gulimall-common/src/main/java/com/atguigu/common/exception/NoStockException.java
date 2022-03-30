package com.atguigu.common.exception;

/**
 * @program: gulimall
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-26 10:57
 **/
public class NoStockException extends RuntimeException {
    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    private Long skuId;
    public NoStockException(Long skuId){
        super("商品"+skuId+"没有足够的库存");
    }

}
