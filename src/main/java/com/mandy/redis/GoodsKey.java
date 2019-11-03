package com.mandy.redis;

/**
 * Created by MandyOu on 2019/10/28
 */
public class GoodsKey extends BasePrefix{

    private GoodsKey(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
}
