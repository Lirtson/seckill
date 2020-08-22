package com.lirtson.seckill.rabbitmq;

import com.lirtson.seckill.domain.SeckillOrder;
import com.lirtson.seckill.domain.User;
import com.lirtson.seckill.model.GoodsVo;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.service.GoodsService;
import com.lirtson.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class MQReceiver {

	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
		
	@Resource
	RedisService redisService;
		
	@Resource
	GoodsService goodsService;

	@Resource
	SeckillService seckillService;

		
	@RabbitListener(queues=MQConfig.SECKILL_QUEUE)
	public void receive(String message) {
		log.info("receive message:"+message);
		SeckillMessage mm  = RedisService.stringToBean(message, SeckillMessage.class);
		String userId = mm.getUserId();
		long goodsId = mm.getGoodsId();
			
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0) {
			return;
		}
		//判断是否已经秒杀到了
		SeckillOrder order = seckillService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {
			return;
		}
		//减库存 下订单 写入秒杀订单
	    	seckillService.seckill(userId, goods);
		}
		
}
