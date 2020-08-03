package com.lirtson.seckill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjaxResponse {

    private int code;
    private Object data;


    public static AjaxResponse success(Object data) {
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setCode(200);
        resultBean.setData(data);
        return resultBean;
    }

    //设置返回码
    public static AjaxResponse fail(int code){
        AjaxResponse resultBean=new AjaxResponse();
        resultBean.setCode(code);
        return resultBean;
    }
    public static AjaxResponse fail(){
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setCode(400);
        return resultBean;
    }

    //请求出现异常时的响应数据封装
    public static AjaxResponse error(Exception e) {
        AjaxResponse resultBean = new AjaxResponse();
        //resultBean.setCode(e.);
        return resultBean;
    }

}