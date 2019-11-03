package com.mandy.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * Created by MandyOu on 2019/10/17
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (Objects.isNull(str) || str.length() < 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if(seconds <= 0){
                jedis.set(realKey,str);
            }else{
                jedis.setex(realKey,seconds,str);
            }
//            jedis.set(realKey, str);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 判断Key是否存在
     */
    public <T> boolean exits(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally{
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally{
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally{
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     */
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            long ret = jedis.del(realKey);
            return ret > 0;
        }finally{
            returnToPool(jedis);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if (Objects.isNull(str) || str.length() <= 0 || Objects.isNull(clazz)) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    private <T> String beanToString(T value) {
        if (Objects.isNull(value)) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
