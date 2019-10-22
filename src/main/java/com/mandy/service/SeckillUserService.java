package com.mandy.service;

import com.mandy.dao.SeckillUserDao;
import com.mandy.domain.SeckillUser;
import com.mandy.exception.GlobalException;
import com.mandy.redis.RedisService;
import com.mandy.redis.SeckillUserKey;
import com.mandy.result.CodeMsg;
import com.mandy.util.MD5Util;
import com.mandy.util.UUIDUtil;
import com.mandy.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Created by MandyOu on 2019/10/20
 */
@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    public SeckillUser getById(long id){
        return seckillUserDao.getById(id);
    }

    //遇到异常时直接抛出，让异常处理器去拦截，然后处理
    //这里由本来的return CodeMsg对象，到现在改成boolean，就是为了让我们专注业务，错误信息让异常处理器去处理
    public boolean login(HttpServletResponse response, LoginVo loginVo){
        if(Objects.isNull(loginVo)){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SeckillUser user = getById(Long.parseLong(mobile));
        if(Objects.isNull(user)){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return true;
    }

    public SeckillUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserKey.token,token,SeckillUser.class);
        if(!Objects.isNull(user)){
            addCookie(response,token,user);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response,String token,SeckillUser user){
        redisService.set(SeckillUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
