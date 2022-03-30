package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: gulimall
 * @description: 购物车的服务接口
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:35
 **/
@Service
public interface CartService {
    /**
     * 将商品添加到购物车
     * @params: Long，Integer
     * @return:  CartItem
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    CartItem addToCart(Long skuId, Integer num);
    /**
     * 从购物车车获得商品
     * @params: Long，Integer
     * @return:  CartItem
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    CartItem getCartItem(Long skuId);

    /**
     * 获取整个购物车
     * @params:
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    Cart getCart();

    void clearCart(String cartKey);

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    void checkItemAll(Integer flag);

    List<CartItem> getUserCartItems();
}
