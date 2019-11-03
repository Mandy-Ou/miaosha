package com.mandy.dao;

import com.mandy.domain.SeckillUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by MandyOu on 2019/10/20
 */
//@Mapper
public interface SeckillUserDao {

    @Select("select * from seckill_user where id=#{id}")
    public SeckillUser getById(@Param("id") long id);

    @Update("update seckill_user set password=#{password} where id=#{id}")
    void update(SeckillUser toBeUpdate);
}
