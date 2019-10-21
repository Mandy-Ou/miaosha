package com.mandy.controller;

import com.mandy.domain.SeckillUser;
import com.mandy.redis.RedisService;
import com.mandy.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by MandyOu on 2019/10/21
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_list")
    public String list(HttpServletResponse response,Model model,
                         @CookieValue(value=SeckillUserService.COOKIE_NAME_TOKEN,required = false)String cookieToken,
                         @RequestParam(value=SeckillUserService.COOKIE_NAME_TOKEN,required = false)String paramToken){
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        SeckillUser user = seckillUserService.getByToken(response,token);
        model.addAttribute("user",user);
        return "goods_list";
    }
}
