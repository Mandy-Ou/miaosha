package com.mandy.redis;

/**
 * Created by MandyOu on 2019/11/14
 */
public class SeckillKey extends BasePrefix{

    private SeckillKey(String prefix) {
        super(prefix);
    }

    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go");
    public static SeckillKey getSeckillPath = new SeckillKey(60,"sp");
    public static SeckillKey getSeckillVerifyCode = new SeckillKey(300,"vc");

}
