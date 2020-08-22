package com.lirtson.seckill.exception;

public enum CustomExceptionType {
    USER_INPUT_ERROR(400,"用户输入异常"),

    SYSTEM_ERROR (500,"系统服务异常"),

    NOT_FOUND_ERROR (404,"页面找不到异常"),

    OTHER_ERROR(999,"其他未知异常"),

    NOT_LOGIN(401,"用户没有权限异常"),

    FORBID_WRITE(403,"禁止写入异常"),
    /*
    以下自定义异常
     */
    SECKILL_OVER(5001,"秒杀完了"),
    REPEATE_SECKILL(5002,"重复秒杀"),
    ORDER_NOT_EXIST(5003,"订单不存在");


    CustomExceptionType(int code, String typeDesc) {
        this.code = code;
        this.typeDesc = typeDesc;
    }

    private String typeDesc;//异常类型中文描述

    private int code; //code

    public String getTypeDesc() {
        return typeDesc;
    }

    public int getCode() {
        return code;
    }

}
