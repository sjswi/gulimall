package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.inteceptor.CartInteceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @description: 购物车服务的实现类
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:35
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    private final String CART_PREFIX = "gulimall:cart:";
    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        R skuInfo =  productFeignService.info(skuId);
        SkuInfoVo skuInfo1 = skuInfo.getData(new TypeReference<SkuInfoVo>(){});
        String o1 = (String) cartOps.get(skuId.toString());
        if(o1==null){
            //        添加新商品
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo1.getSkuDefaultImg());
                cartItem.setSkuId(skuId);
                cartItem.setTitle(skuInfo1.getSkuTitle());
                cartItem.setPrice(skuInfo1.getPrice());
            }, threadPoolExecutor);
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(skuSaleAttrValues);
            }, threadPoolExecutor);
            try {
                CompletableFuture.allOf(future,getSkuSaleAttrValues).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            String o = JSON.toJSONString(cartItem);
            cartOps.put(String.valueOf(skuId), o);
            return cartItem;
        }else{
            //购物车有此商品，因此只需修改商品数量即可
            CartItem cartItem = JSON.parseObject(o1, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);
            String string = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),string);
            return cartItem;
        }


    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;

    }

    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInteceptor.threadLocal.get();
        List<CartItem> cartItems = null;
        if(userInfoTo.getUserId()!=null){
            //在线账户
            String cartKey = CART_PREFIX+userInfoTo.getUserId();
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            //获得临时的购物车并与登录的账户进行合并
            if(tempCartItems!=null&&tempCartItems.size()>0){
                for(CartItem item:tempCartItems){
                    addToCart(item.getSkuId(), item.getCount());
                }
            }
            //获得登录后的购物车数据
            cartItems = getCartItems(cartKey);

//          清除临时购物车
            clearCart(tempCartKey);
        }else{
            //离线账户
            String cartKey = CART_PREFIX+userInfoTo.getUserKey();
            cartItems = getCartItems(cartKey);

        }
        if(cartItems!=null&&cartItems.size()>0) {
            int n = 0;

            for (CartItem cartItem : cartItems) {
                if (!cartItem.getCheck()) {
                    break;
                }
                n++;
            }
            if (n == cartItems.size()) {
                cart.setCheckAll(true);
            }
        }
        cart.setItems(cartItems);
//          清除临时购物车

        return cart;
    }


    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String string = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), string);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String string = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), string);

    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public void checkItemAll(Integer flag) {
        Cart cart = getCart();
        cart.setCheckAll(flag==1);;
        UserInfoTo userInfoTo = CartInteceptor.threadLocal.get();
        String cartKey = CART_PREFIX+userInfoTo.getUserId();
        String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        if(userInfoTo.getUserId()==null){
            clearCart(tempCartKey);

            cart.getItems().forEach((item)->{
                String string = JSON.toJSONString(item);
                cartOps.put(item.getSkuId().toString(), string);
            });


        }else{
            clearCart(cartKey);

            cart.getItems().forEach((item)->{
                String string = JSON.toJSONString(item);
                cartOps.put(item.getSkuId().toString(), string);
            });
        }


    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInteceptor.threadLocal.get();
        if(userInfoTo.getUserId()==null){
            return null;
        }
        String cartKey = CART_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);
        return cartItems.stream().filter(CartItem::getCheck).map(item->{
            item.setPrice(productFeignService.getPrice(item.getSkuId()));
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * @params: 获取要操作的购物车
     * @return:
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInteceptor.threadLocal.get();
        String cartKey = "";
        if(userInfoTo.getUserId()!=null){
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else{
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values!=null&&values.size()>0) {
            return values.stream().map((obj) -> {
                String str = (String) obj;

                return JSON.parseObject(str, CartItem.class);
            }).collect(Collectors.toList());
        }
        return null;
    }
}
