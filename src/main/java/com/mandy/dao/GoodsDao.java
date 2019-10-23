package com.mandy.dao;

import com.mandy.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by MandyOu on 2019/10/22
 */
public interface GoodsDao {

    @Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date from seckill_goods sg left join goods g on sg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date from seckill_goods sg left join goods g on sg.goods_id = g.id where g.id=#{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);
}
