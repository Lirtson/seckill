package com.lirtson.seckill.dao;

import java.util.List;

import com.lirtson.seckill.domain.Goods;
import com.lirtson.seckill.domain.SeckillGoods;
import com.lirtson.seckill.model.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface GoodsDao {

	@Select("select g.*,sk.stock_count, sk.start_date, sk.end_date,sk.seckill_price from seckill_goods sk left join goods g on sk.goods_id = g.id")
	public List<GoodsVo> listGoodsVo();

	@Select("select g.*,sk.stock_count, sk.start_date, sk.end_date,sk.seckill_price from seckill_goods sk left join goods g on sk.goods_id = g.id where g.id = #{goodsId}")
	public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

	//@Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    //public int reduceStock(SeckillGoods g);

	@Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId}")
	public int reduceStock(@Param("goodsId")long goodsId);

	@Update("update seckill_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
	public int resetStock(SeckillGoods g);

	
}
