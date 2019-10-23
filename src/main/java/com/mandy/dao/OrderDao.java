package com.mandy.dao;

import com.mandy.domain.OrderInfo;
import com.mandy.domain.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

/**
 * Created by MandyOu on 2019/10/23
 */
public interface OrderDao {

    @Select("select * from seckill_order where user_id=#{userId} and goods_id=#{goodsId}")
    public SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId")Long userId, @Param("goodsId")long goodsId);

    @Insert("insert into order_info values(default,#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate},null)")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    public long insert(OrderInfo orderInfo);

    @Insert("insert into seckill_order values(default,#{userId},#{orderId},#{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);
}
