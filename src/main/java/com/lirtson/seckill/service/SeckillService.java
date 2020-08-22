package com.lirtson.seckill.service;

import com.lirtson.seckill.dao.OrderDao;
import com.lirtson.seckill.domain.OrderInfo;
import com.lirtson.seckill.domain.SeckillOrder;
import com.lirtson.seckill.domain.User;
import com.lirtson.seckill.model.AjaxResponse;
import com.lirtson.seckill.model.GoodsVo;
import com.lirtson.seckill.redis.GoodsKey;
import com.lirtson.seckill.redis.OrderKey;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.redis.SeckillKey;
import com.lirtson.seckill.util.MD5Util;
import com.lirtson.seckill.util.UUIDUtil;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SeckillService {
    @Resource
    RedisService redisService;
    @Resource
    GoodsService goodsService;
    @Resource
    OrderDao orderDao;
    @Resource
    OrderService orderService;

    //初始化
    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    //生成秒杀地址
    public String createSeckillPath(String userId, long goodsId) {
        if(userId == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getSeckillPath, ""+userId + "_"+ goodsId, str);
        return str;
    }
    //检查秒杀地址是否正确
    public boolean checkPath(String userId, long goodsId, String path) {
        if(userId==""||userId == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+userId + "_"+ goodsId, String.class);
        return path.equals(pathOld);
    }

    public SeckillOrder getSeckillOrderByUserIdGoodsId(String userId, long goodsId) {
        return redisService.get(OrderKey.getSeckillOrderByUidGid, ""+userId+"_"+goodsId,SeckillOrder.class);
    }


    @Transactional
    public OrderInfo seckill(String userId, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            //order_info seckill_order
            return createOrder(userId, goods);
        }else {
            setGoodsOver(goods.getId());//这里还没写
            return null;
        }
    }

    @Transactional
    public OrderInfo createOrder(String userId, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(Long.parseLong(userId));//String转Long
        orderDao.insert(orderInfo);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(Long.parseLong(userId));//
        orderDao.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, ""+userId+"_"+goods.getId(),seckillOrder);

        return orderInfo;
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }

    /**
     * 秒杀结果
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    public long getSeckillResult(String userId, long goodsId) {
        SeckillOrder order = getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {//秒杀成功
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }
}
