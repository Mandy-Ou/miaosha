package com.mandy.controller;

import com.mandy.domain.SeckillUser;
import com.mandy.redis.RedisService;
import com.mandy.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String list(Model model, SeckillUser user){
        model.addAttribute("user",user);
        return "goods_list";
    }
}
