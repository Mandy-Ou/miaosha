package com.mandy.redis;

/**
 * Created by MandyOu on 2019/10/17
 */
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
