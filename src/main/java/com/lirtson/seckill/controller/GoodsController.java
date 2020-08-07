package com.lirtson.seckill.controller;

import com.lirtson.seckill.model.AjaxResponse;
import com.lirtson.seckill.model.GoodsVo;
import com.lirtson.seckill.redis.GoodsKey;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.service.GoodsService;
import com.lirtson.seckill.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


@Controller
@RequestMapping("seckill/goods")
public class GoodsController {

    @Resource
    UserService userService;

    @Resource
    RedisService redisService;

    @Resource
    GoodsService goodsService;


    @GetMapping()
    @ResponseBody
    public AjaxResponse getGoodsVo(){
        /*
        List<GoodsVo> s = redisService.get(GoodsKey.getGoodsList, "", String.class);
    	if(!(s==null||s.isEmpty())) {
    		return AjaxResponse.success(s);
    	}
    	s=goodsService.listGoodsVo();
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
         */
        return AjaxResponse.success(goodsService.listGoodsVo());
    }


    @GetMapping("/{goodsId}")
    @ResponseBody
    public AjaxResponse goodsVoDetail(@PathVariable("goodsId")long goodsId) {
        //取缓存
        String s = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(s)) {
            return AjaxResponse.success(s);
        }

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }


        if(!StringUtils.isEmpty(s)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, s);
        }
        return AjaxResponse.success(goods);
    }

}
