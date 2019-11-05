package com.mandy.controller;

import com.mandy.domain.OrderInfo;
import com.mandy.domain.SeckillOrder;
import com.mandy.domain.SeckillUser;
import com.mandy.redis.RedisService;
import com.mandy.result.CodeMsg;
import com.mandy.result.Result;
import com.mandy.service.GoodsService;
import com.mandy.service.OrderService;
import com.mandy.service.SeckillService;
import com.mandy.service.SeckillUserService;
import com.mandy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * Created by MandyOu on 2019/10/23
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    /**
     * GET POST有什么区别？
     * GET是幂等的,不会修改数据库。
     * */
    @RequestMapping(value = "/do_seckill",method= RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> seckill(Model model, SeckillUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (Objects.isNull(user)) {
            return Result.error(CodeMsg.SESSION_ERROR);
//            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
//            model.addAttribute("errMsg", CodeMsg.SECKILL_OVER.getMsg());
//            return "seckill_fail"; //这里改到了从前端页面来进行跳转，而不由后台程序来进行页面跳转！！！
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (Objects.nonNull(order)) {
//            model.addAttribute("errMsg", CodeMsg.REPEATE_SECKILL.getMsg());
//            return "seckill_fail";
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //减库存 下订单 写入秒杀订单 （这三步操作应该是一个事务）
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
//        return "order_detail";
        return Result.success(orderInfo);
    }
}
