package com.lirtson.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lirtson.seckill.model.AjaxResponse;
import com.lirtson.seckill.model.GoodsVo;
import com.lirtson.seckill.redis.GoodsKey;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.service.GoodsService;
import com.lirtson.seckill.service.SeckillService;
import com.lirtson.seckill.service.UserService;
import com.lirtson.seckill.util.JWTUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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


    @GetMapping("/test")
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
        return AjaxResponse.success("无语1");
    }

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
