package com.lirtson.seckill.service;

import com.lirtson.seckill.dao.UserDao;
import com.lirtson.seckill.domain.User;
import com.lirtson.seckill.exception.CustomException;
import com.lirtson.seckill.exception.CustomExceptionType;
import com.lirtson.seckill.model.UserVO;
import com.lirtson.seckill.redis.RedisService;
import com.lirtson.seckill.redis.UserKey;
import com.lirtson.seckill.util.JWTUtils;
import com.lirtson.seckill.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Resource
    UserDao userDao;
    @Resource
    RedisService redisService;

    public User getById(long id){
        //取缓存
        System.out.println("从缓存取user");
        User user = redisService.get(UserKey.getById, ""+id, User.class);
        if(user != null) {
            return user;
        }
        //取数据库
        System.out.println("从数据库取user");
        user = userDao.getById(id);
        if(user != null) {
            redisService.set(UserKey.getById, ""+id, user);
        }
        System.out.println("");
        return user;
    }

    public String login(UserVO userVo) {
        if(userVo == null) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR,"服务端异常");
        }
        Long userId = userVo.getId();
        String formPass = userVo.getPassword();
        //判断账号是否存在
        User user = getById(userId);
        if(user == null) {
            throw new CustomException(CustomExceptionType.NOT_LOGIN,"账号不存在");
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new CustomException(CustomExceptionType.NOT_LOGIN,"密码错误");
        }
        //生成token
        String token= getToken(userId);
        return token;
    }

    public User getByToken(String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        /*延长有效期
        if(user != null) {

        }
         */
        return user;
    }

    public String getToken(Long userId) {
        //存入JWT的payload中生成token
        Map claims = new HashMap<String,Integer>();
        claims.put("user_userId",userId);
        String subject = "use";
        String token = null;
        try {
            //该token过期时间为12h
            token = JWTUtils.createJWT(claims, subject, 1000*60*60*12 );
        } catch (Exception e) {
            throw new RuntimeException("创建Token失败");
        }
        return token;
    }
}
