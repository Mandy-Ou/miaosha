package com.mandy.rabbitmq;

import com.mandy.domain.SeckillOrder;
import com.mandy.domain.SeckillUser;
import com.mandy.redis.RedisService;
import com.mandy.service.GoodsService;
import com.mandy.service.OrderService;
import com.mandy.service.SeckillService;
import com.mandy.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by MandyOu on 2019/11/13
 */
@Service
public class MQReciver {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    private static Logger logger = LoggerFactory.getLogger(MQReciver.class);

    @RabbitListener(queues=MQConfig.SECKILL_QUEUE)
    public void receiveSeckill(String message){
        logger.info("receive message:" + message);
        SeckillMessage seckillMsg = RedisService.stringToBean(message,SeckillMessage.class);
        SeckillUser seckillUser = seckillMsg.getUser();
        long goodsId = seckillMsg.getGoodsId();

        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (Objects.nonNull(order)) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        seckillService.seckill(seckillUser, goods);
    }

    /*
    @RabbitListener(queues=MQConfig.QUEUE)
    public void receive(String message){
        logger.info("receive message:" + message);
    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        logger.info("receive topic queue1 message:" + message);
    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){
        logger.info("receive topic queue2 message:" + message);
    }

    @RabbitListener(queues=MQConfig.HEADER_QUEUE)
    public void receiveHeaderQueue(byte[] message){
        logger.info("header queue1 message:" + new String(message));
    }
*/




}
