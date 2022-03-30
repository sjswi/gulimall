package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.inteceptor.CartInteceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * @program: gulimall
 * @description: 购物车Controller
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-22 12:38
 **/
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItem> getCurrentCartItems(){
        return cartService.getUserCartItems();
    }

    /**
     * 浏览器中存有一个cookie，这个cookie的过期时间是一个月，没有user-key就会分配一个user-key
     * 这个操作可以使用inteceptor来完成。
     * 具体使用一个拦截器，在拦截器中获取session看是否登陆，cookie看是否存在临时用户
     * @params: HttpSession
     * @return: String
     * @author: yuxiaobing
     * @date: 2022/3/22
     **/
    @GetMapping("/cart.html")
    public String cartListPage(Model model){
        /**
         * 使用ThreadLocal来获取userInfoTo进而知道当前用户是在线（登陆了）还是离线用户
         * **/
//        UserInfoTo userInfoTo = CartInteceptor.threadLocal.get();
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
//        System.out.println(userInfoTo);
        return "cartList";

    }

     /**
      * 添加商品到购物车
      * RedirectAttributes addFlashAttribute ,将数据放入session中只能取一次
      * addAttribute
      * @params:
      * @return:
      * @author: yuxiaobing
      * @date: 2022/3/22
      **/
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,@RequestParam("num")  Integer num, RedirectAttributes model){
        CartItem cartItem = cartService.addToCart(skuId,num);
        model.addAttribute("skuId", skuId);
        //        为了维护幂等性，分为两个页面，将请求获得结果后重定向到其他页面
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);

        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId, num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/checkItemAll")
    public String checkItemAll(@RequestParam("flag") Integer flag){

        cartService.checkItemAll(flag);

        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
