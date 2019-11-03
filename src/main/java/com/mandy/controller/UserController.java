package com.mandy.controller;

import com.mandy.domain.SeckillUser;
import com.mandy.redis.RedisService;
import com.mandy.result.Result;
import com.mandy.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 压测接口
 * Created by MandyOu on 2019/10/24
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser user){
        return Result.success(user);
    }


}
