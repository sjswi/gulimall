package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @program: gulimall
 * @description: 订单确认页需要的数据
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:20
 **/

public class OrderConfirmVo {


    //封装收货地址列表
    @Setter @Getter
    private List<MemberAddressVo> addressVoList;

    @Setter @Getter
    private List<OrderItemVo> itemVos;


    //优惠券信息
    @Setter @Getter
    private Integer integrations;
    private Map<Long, Boolean> stocks;

    public BigDecimal getTotal(){
        BigDecimal sum = new BigDecimal(0);
        if(itemVos!=null){
            for(OrderItemVo orderItemVo:itemVos){
                BigDecimal multiply = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount()));
                sum=sum.add(multiply);
            }
        }
        return sum;
    }


    public BigDecimal getPayPrice(){
        return this.getTotal();
    }

    @Getter @Setter
    private String orderToken;

    public Integer getCount(){
        Integer sum = 0;
        if(itemVos!=null){
            for(OrderItemVo orderItemVo:itemVos){
                sum+=orderItemVo.getCount();
            }
        }
        return sum;
    }

    public void setStocks(Map<Long, Boolean> stocks) {
        this.stocks = stocks;
    }

    public Map<Long, Boolean> getStocks() {
        return stocks;
    }
}
