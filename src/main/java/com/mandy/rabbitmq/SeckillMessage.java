package com.mandy.rabbitmq;

import com.mandy.domain.SeckillUser;

/**
 * Created by MandyOu on 2019/11/14
 */
public class SeckillMessage {
    private SeckillUser user;
    private long goodsId;

    public SeckillUser getUser() {
        return user;
    }

    public void setUser(SeckillUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
