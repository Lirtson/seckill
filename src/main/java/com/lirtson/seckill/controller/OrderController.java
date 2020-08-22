package com.lirtson.seckill.controller;

import com.lirtson.seckill.domain.OrderInfo;
import com.lirtson.seckill.exception.CustomException;
import com.lirtson.seckill.exception.CustomExceptionType;
import com.lirtson.seckill.model.AjaxResponse;
import com.lirtson.seckill.model.GoodsVo;
import com.lirtson.seckill.model.OrderDetailVo;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.service.GoodsService;
import com.lirtson.seckill.service.OrderService;
import com.lirtson.seckill.service.UserService;
import com.lirtson.seckill.util.JWTUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("seckill/order")
public class OrderController {

	@Resource
	UserService userService;
	
	@Resource
	RedisService redisService;
	
	@Resource
	OrderService orderService;
	
	@Resource
	GoodsService goodsService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public AjaxResponse info(HttpServletRequest request,
							 @RequestBody long orderId) {
    	//验证user
		String token = request.getAttribute("token").toString();
		System.out.println("seckillController的token:"+token);
		String userId=null;
		try {
			userId= JWTUtils.getUserId(token);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(userId == null||userId=="") {
			return AjaxResponse.fail(401);
		}
    	OrderInfo order = orderService.getOrderById(orderId);
    	if(order == null) {
			return AjaxResponse.error(new CustomException(CustomExceptionType.ORDER_NOT_EXIST,"订单不存在"));
    	}
    	long goodsId = order.getGoodsId();
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo vo = new OrderDetailVo();
    	vo.setOrder(order);
    	vo.setGoods(goods);
    	return AjaxResponse.success(vo);
    }
    
}