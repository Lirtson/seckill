package com.lirtson.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lirtson.seckill.domain.SeckillOrder;
import com.lirtson.seckill.domain.User;
import com.lirtson.seckill.exception.CustomException;
import com.lirtson.seckill.exception.CustomExceptionType;
import com.lirtson.seckill.model.AjaxResponse;
import com.lirtson.seckill.model.GoodsVo;
import com.lirtson.seckill.rabbitmq.MQSender;
import com.lirtson.seckill.rabbitmq.SeckillMessage;
import com.lirtson.seckill.redis.GoodsKey;
import com.lirtson.seckill.redis.OrderKey;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.redis.SeckillKey;
import com.lirtson.seckill.service.GoodsService;
import com.lirtson.seckill.service.SeckillService;
import com.lirtson.seckill.service.UserService;
import com.lirtson.seckill.util.JWTUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("seckill")
public class SeckillController {

    @Resource
    RedisService redisService;
    @Resource
    SeckillService seckillService;
    @Resource
    GoodsService goodsService;
    @Resource
    MQSender sender;

    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

    //初始化
    @GetMapping("/reset")
    @ResponseBody
    public AjaxResponse reset() {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getSeckillGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getSeckillOrderByUidGid);
        redisService.delete(SeckillKey.isGoodsOver);
        seckillService.reset(goodsList);
        return AjaxResponse.success(null);
    }


    //进行秒杀
    @GetMapping("/{path}/order/{goodsId}")
    @ResponseBody
    public AjaxResponse seckill(HttpServletRequest request,
                                @PathVariable("goodsId") long goodsId,
                                @PathVariable("path") String path) {
        //验证userId
        String token = request.getAttribute("token").toString();
        System.out.println("seckillController的seckill的token:"+token);
        String userId=null;
        try {
            userId=JWTUtils.getUserId(token);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(userId == null) {
            return AjaxResponse.fail(401);
        }
        //验证path
        boolean check = seckillService.checkPath(userId, goodsId, path);
        if(!check){
            System.out.println("/path !check");
            return AjaxResponse.fail(400);
        }
        System.out.println("path正确！");

        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over) {
            //卖完了
            return AjaxResponse.error(new CustomException(CustomExceptionType.SECKILL_OVER,"秒杀完了"));
        }


        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, ""+goodsId);//10
        if(stock < 0) {
            localOverMap.put(goodsId, true);
            return AjaxResponse.error(new CustomException(CustomExceptionType.SECKILL_OVER,"秒杀完了"));
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {
            return AjaxResponse.error(new CustomException(CustomExceptionType.REPEATE_SECKILL,"重复秒杀"));
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUserId(userId);
        mm.setGoodsId(goodsId);
        sender.sendSeckillMessage(mm);//
        //返回状态：排队中
        Map<String,Integer> map=new HashMap<>();
        map.put("状态",0);
        return AjaxResponse.success(map);//排队中
    }

    //获取秒杀结果
    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @GetMapping("/result/{goodsId}")
    @ResponseBody
    public AjaxResponse seckillResult(HttpServletRequest request,
                                      @PathVariable("goodsId") long goodsId) {
        //验证userId
        String token = request.getAttribute("token").toString();
        System.out.println("seckillController的token:"+token);
        String userId=null;
        try {
            userId=JWTUtils.getUserId(token);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(userId == null||userId=="") {
            return AjaxResponse.fail(401);
        }

        long result  =seckillService.getSeckillResult(userId, goodsId);//从redis中看有没有订单
        Map<String,Long> map=new HashMap<>();
        map.put("状态",result);
        return AjaxResponse.success(map);
    }

    //获取秒杀地址
    @GetMapping("/path/{goodsId}")
    @ResponseBody
    public AjaxResponse getSeckillPath(@PathVariable Long goodsId, HttpServletRequest request){
        //User user=redisService.get(UserKey.token, token, User.class);
        System.out.println("。。。。。。。。。。");
        String token = request.getAttribute("token").toString();
        System.out.println("seckillController的token:"+token);
        String userId=null;
        try {
            userId=JWTUtils.getUserId(token);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(userId);
        if(userId == null) {
            return AjaxResponse.fail(401);
        }
        String path  =seckillService.createSeckillPath(userId, goodsId);
        System.out.println(path);
        Map<String,String> map=new HashMap<>();
        map.put("path",path);
        return AjaxResponse.success(map);
    }




}
