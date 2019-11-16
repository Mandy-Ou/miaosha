package com.mandy.redis;

/**
 * Created by MandyOu on 2019/10/17
 */
public class OrderKey extends BasePrefix{

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey(0,"seckillOUG");
}
