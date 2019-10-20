package com.mandy.controller;

import com.mandy.redis.RedisService;
import com.mandy.result.CodeMsg;
import com.mandy.result.Result;
import com.mandy.service.SeckillUserService;
import com.mandy.util.ValidatorUtil;
import com.mandy.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

/**
 * Created by MandyOu on 2019/10/19
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(LoginVo loginVo) {
        log.info(loginVo.toString());
        //参数校验
        String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if(StringUtils.isEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if(StringUtils.isEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }

        //登录
        CodeMsg cm = seckillUserService.login(loginVo);
        if(cm.getCode() == 0){
            return Result.success(true);
        }else{
            return Result.error(cm);
        }
    }
}
