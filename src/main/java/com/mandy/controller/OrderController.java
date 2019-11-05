package com.mandy.controller;

import com.mandy.domain.OrderInfo;
import com.mandy.domain.SeckillUser;
import com.mandy.redis.RedisService;
import com.mandy.result.CodeMsg;
import com.mandy.result.Result;
import com.mandy.service.GoodsService;
import com.mandy.service.OrderService;
import com.mandy.service.SeckillUserService;
import com.mandy.vo.GoodsVo;
import com.mandy.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * Created by MandyOu on 2019/11/5
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, SeckillUser user, @RequestParam("orderId")long orderId){
        //这些数据判断可以创建一个拦截器来判断，比如定义@NeedLogin
        if(Objects.isNull(user)){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(Objects.isNull(order)){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoodsVo(goods);
        vo.setOrder(order);
        return Result.success(vo);
    }

}
