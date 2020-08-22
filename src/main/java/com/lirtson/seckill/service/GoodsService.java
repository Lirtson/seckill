package com.lirtson.seckill.service;

import com.lirtson.seckill.dao.GoodsDao;
import com.lirtson.seckill.domain.Goods;
import com.lirtson.seckill.domain.SeckillGoods;
import com.lirtson.seckill.model.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsService {
    @Resource
    GoodsDao goodsDao;
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        //SeckillGoods g = new SeckillGoods();
        //g.setId(goods.getId());
        int ret = goodsDao.reduceStock(goods.getId());//ret=1
        return ret > 0;
    }

    public void resetStock(List<GoodsVo> goodsList) {
        for(GoodsVo goods : goodsList ) {
            SeckillGoods g = new SeckillGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
