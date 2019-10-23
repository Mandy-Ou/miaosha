package com.mandy.service;

import com.mandy.domain.OrderInfo;
import com.mandy.domain.SeckillUser;
import com.mandy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by MandyOu on 2019/10/23
 */
@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;


    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        goodsService.reduceStock(goods);
        //order_info seckill_order
        return orderService.createOrder(user,goods);
    }
}
