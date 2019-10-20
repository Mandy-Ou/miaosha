package com.mandy.service;

import com.mandy.dao.SeckillUserDao;
import com.mandy.domain.SeckillUser;
import com.mandy.result.CodeMsg;
import com.mandy.util.MD5Util;
import com.mandy.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by MandyOu on 2019/10/20
 */
@Service
public class SeckillUserService {

    @Autowired
    SeckillUserDao seckillUserDao;

    public SeckillUser getById(long id){
        return seckillUserDao.getById(id);
    }

    public CodeMsg login(LoginVo loginVo){
        if(Objects.isNull(loginVo)){
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SeckillUser user = getById(Long.parseLong(mobile));
        if(Objects.isNull(user)){
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDB);
        if(!calcPass.equals(dbPass)){
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }

}
