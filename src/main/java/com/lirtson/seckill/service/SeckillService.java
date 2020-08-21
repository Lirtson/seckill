package com.lirtson.seckill.service;

import com.lirtson.seckill.domain.User;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.redis.SeckillKey;
import com.lirtson.seckill.util.MD5Util;
import com.lirtson.seckill.util.UUIDUtil;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillService {
    @Resource
    RedisService redisService;
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
}
