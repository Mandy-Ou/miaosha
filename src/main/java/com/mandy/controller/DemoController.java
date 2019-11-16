package com.mandy.controller;

import com.mandy.domain.User;
import com.mandy.rabbitmq.MQSender;
import com.mandy.redis.RedisService;
import com.mandy.redis.UserKey;
import com.mandy.result.CodeMsg;
import com.mandy.result.Result;
import com.mandy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by MandyOu on 2019/10/9
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

/*
    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        sender.send("hello,Mandy");
        return Result.success("hello,RabbitMQ (simple test)");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> mqTopic(){
        sender.sendTopic("hello,Mandy");
        return Result.success("hello,RabbitMQ (topic test)");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> mqFanout(){
        sender.sendFanout("hello,Mandy");
        return Result.success("hello,RabbitMQ (fanout test)");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> mqHeader(){
        sender.sendHeader("hello,Mandy");
        return Result.success("hello,RabbitMQ (header test)");
    }
*/


    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello,imooc");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "Mandy");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("11111");
        redisService.set(UserKey.getById,""+1,user);
        return Result.success(true);
    }

}
