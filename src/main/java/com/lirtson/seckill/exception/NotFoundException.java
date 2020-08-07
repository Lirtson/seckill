package com.lirtson.seckill.exception;

import com.lirtson.seckill.model.AjaxResponse;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//全局异常处理捕获不到404异常
@RestController
public class NotFoundException implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(ERROR_PATH)
    public AjaxResponse error(){
        return AjaxResponse.fail(404);
    }
}
