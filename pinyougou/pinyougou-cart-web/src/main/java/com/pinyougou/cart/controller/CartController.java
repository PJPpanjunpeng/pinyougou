package com.pinyougou.cart.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    /**
     * 获取用户名
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
