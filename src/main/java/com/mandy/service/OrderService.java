package com.mandy.service;

import com.mandy.dao.OrderDao;
import com.mandy.domain.OrderInfo;
import com.mandy.domain.SeckillOrder;
import com.mandy.domain.SeckillUser;
import com.mandy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by MandyOu on 2019/10/23
 */
@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public SeckillOrder getSeckillOrderByUserIdGoodsId(Long userId, long goodsId) {
        return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
    }

    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setCreateDate(new Date());
        long orderId = orderDao.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setGoodsId(goods.getId());
        orderDao.insertSeckillOrder(seckillOrder);
        return orderInfo;
    }
}
