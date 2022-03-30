package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * vo中含有需要计算的属性
 * @program: gulimall
 * @description: 购物车
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:23
 **/
public class Cart {
    List<CartItem> items;

    private Boolean checkAll=false;

    public Boolean getCheckAll() {
        return checkAll;
    }

    public void setCheckAll(Boolean checkAll) {
        if(items!=null&&items.size()>0){
            for (CartItem item:items){
                item.setCheck(checkAll);
            }
        }
        this.checkAll = checkAll;
    }

    private Integer count;//商品数量

    private Integer countType;//商品类型数量

    private BigDecimal amount;//总价

    private BigDecimal reduce = new BigDecimal(0);//减免

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCount() {
        int co=0;
        if(items!=null&&items.size()>0){
            for (CartItem item:items){
                co+= item.getCount();
            }
        }
        this.count=co;
        return co;
    }



    public Integer getCountType() {
        int co=0;
        if(items!=null&&items.size()>0){
            co+=1;
        }
        this.countType=co;
        return co;
    }


    public BigDecimal getAmount() {
        BigDecimal bigDecimal = new BigDecimal(0);
//        1、计算购物项总价
        if(items!=null&&items.size()>0){
            for (CartItem item:items){
                if(item.getCheck()) {
                    bigDecimal = bigDecimal.add(item.getTotalPrice());
                }
            }
        }
        this.amount=bigDecimal;
        return bigDecimal.subtract(getReduce());

    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
