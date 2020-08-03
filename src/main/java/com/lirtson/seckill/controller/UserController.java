package com.lirtson.seckill.controller;

import com.lirtson.seckill.domain.User;
import com.lirtson.seckill.model.AjaxResponse;
import com.lirtson.seckill.model.UserVO;
import com.lirtson.seckill.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/seckill/user")
public class UserController {
    @Resource
    UserService userService;
    private static Logger log = LoggerFactory.getLogger(UserController.class);
    @PostMapping()
    @ResponseBody
    public AjaxResponse login(@RequestBody UserVO user){
        String token=userService.login(user);
        if(token==null)
            return AjaxResponse.fail();
        Map map = new HashMap<String, String>();
        map.put("token", token);
        return AjaxResponse.success(map);
    }
}
