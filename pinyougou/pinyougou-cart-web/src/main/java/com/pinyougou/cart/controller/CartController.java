package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //Cookie 中购物车列表的名称
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";

    //Cookie 中购物车列表的最大生存时间，一天
    private static final int COOKIE_CART_LIST_MAX_AGE = 3600 * 24;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    /**
     * 实现登录、未登录下将商品加入购物车列表
     *
     * @param itemId
     * @param num
     * @return
     */
    @GetMapping("/addItemToCartList")
    public Result addItemToCartList(Long itemId, Integer num) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将商品加入到购物车列表
            List<Cart> newCartList = cartService.addItemToCartList(cartList, itemId, num);

            if ("anonymousUser".equals(username)) {
                //未登录，将商品加入写回到Cookie
                String cartListJsonStr = JSON.toJSONString(newCartList);
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST, cartListJsonStr, COOKIE_CART_LIST_MAX_AGE, true);
            } else {
                //已登录，将商品加入写回到redis
            }
            return Result.ok("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("加入购物车失败");

    }


    /**
     * 获取购物车列表数据；如果登录了则从 redis 中获取，若未登录则从 cookie 中获取
     *
     * @return
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            //未登录，从 cookie 中获取购物车列表数据
            List<Cart> cookie_cartList = new ArrayList<>();

            //1、获取cookie中购物车数据
            String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
            System.out.println(cartListJsonStr);

            //2、将cookie的购物车json格式字符串转换为集合
            if (!StringUtils.isEmpty(cartListJsonStr)) {
                cookie_cartList = JSONArray.parseArray(cartListJsonStr, Cart.class);
                return cookie_cartList;
            } else {
                return cookie_cartList;
            }
        } else {
            //已登录，从 redis 中获取购物车列表数据
            return null;
        }
    }


    /**
     * 获取用户名
     *
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername() {
        Map<String, Object> map = new HashMap<>();

        //如果是配置了IS_AUTHENTICATED_ANONYMOUSLY则在没有登录的情况下返回的username的值为anonymousUser
        //如果注册的时候用户名就为anonymousUser；则如何区分是否已经登录？
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //如果未登录；那么获取到的username为：anonymousUser
        map.put("username", username);
        return map;
    }

}
