package com.mandy.controller;

import com.mandy.domain.SeckillOrder;
import com.mandy.domain.SeckillUser;
import com.mandy.rabbitmq.MQSender;
import com.mandy.rabbitmq.SeckillMessage;
import com.mandy.redis.GoodsKey;
import com.mandy.redis.OrderKey;
import com.mandy.redis.RedisService;
import com.mandy.redis.SeckillKey;
import com.mandy.result.CodeMsg;
import com.mandy.result.Result;
import com.mandy.service.GoodsService;
import com.mandy.service.OrderService;
import com.mandy.service.SeckillService;
import com.mandy.service.SeckillUserService;
import com.mandy.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by MandyOu on 2019/10/23
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

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

    @Autowired
    MQSender sender;

    //用来存放商品是否已经被秒杀完毕
    private Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();


    /**
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取秒杀商品的库存数量
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(Objects.isNull(goodsList)){
            return;
        }
        for(GoodsVo goods:goodsList){
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

    /**
     *
     * QPS:603
     * 500*10
     *
     * QPS:769
     * 800*10
     *
     * GET POST有什么区别？
     * GET是幂等的,不会修改数据库。
     * */
    @RequestMapping(value = "/{path}/do_seckill",method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckill(Model model, SeckillUser user,
                                   @RequestParam("goodsId") long goodsId,
                                   @PathVariable("path")String path) {
        model.addAttribute("user", user);
        if (Objects.isNull(user)) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = seckillService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //判断秒杀的商品是否已经秒杀结束了
        //先判断，减少访问Redis的次数
        boolean isOver = localOverMap.get(goodsId);
        if(isOver){
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //判断是否已经秒杀过这个商品了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (Objects.nonNull(order)) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //入队
        SeckillMessage seckillMsg = new SeckillMessage();
        seckillMsg.setUser(user);
        seckillMsg.setGoodsId(goodsId);
        sender.sendSeckillMessage(seckillMsg);
        return Result.success(0);//排队中

        /*
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (Objects.nonNull(order)) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //减库存 下订单 写入秒杀订单 （这三步操作应该是一个事务）
        //这里可以设置判断，如果抛异常，则删除Redis中相应的缓存
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return Result.success(orderInfo);
        */
    }

    /**
     * 返回：
     *   orderId   成功
     *   -1        秒杀失败
     *   0         排队中
     *
     */
    @RequestMapping(value = "/result",method= RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, SeckillUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (Objects.isNull(user)) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillService.getSeckillResult(user.getId(),goodsId);
        return Result.success(result);
    }

    @RequestMapping(value="/reset",method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model){
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods :goodsList){
            goods.setStockCount(10);
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goods.getId(),10);
            localOverMap.put(goods.getId(),false);
            redisService.delete(OrderKey.getSeckillOrderByUidGid);
            redisService.delete(SeckillKey.isGoodsOver);
        }
        seckillService.reset(goodsList);
        return Result.success(true);

    }

    @RequestMapping(value="/path",method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(Model model,SeckillUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam("verifyCode")int verifyCode){
        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //校验验证码
        boolean check = seckillService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = seckillService.createSeckillPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/verifyCode",method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, Model model, SeckillUser user, @RequestParam("goodsId")long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = seckillService.createSeckillVerifyCode(user,goodsId);
        try{
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (IOException e){
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }
}
