package com.susstore.result;

/**
 * xx00~xx30分配给用户
 * 200:成功
 *  404:未找到用户/商品
 *  4001:用户未登录
 *  4002:登陆失败
 *  4010:注册失败
 *  4020:参数错误
 *
 *
 *
 */
public enum ResultCode {
    SUCCESS(200, "成功"),
    CREATE_SUCCESS(201,"创建资源成功"),

    FAILED(500, "服务器内部失败"),
    NOT_FOUND(404, "页面不存在"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    USER_NOT_LOGIN(4001,"用户未登录"),
    USER_NOT_ACTIVATED(4005,"用户未激活"),
    USER_NOT_EXIST(4003,"用户不存在"),
    REGISTER_FAIL(4010,"注册失败"),
    LOGIN_FAIL(4002,"登录失败"),
    DEAL_ALREADY_EXISIT(4030,"订单已经存在"),
    DEAL_OFF_SHELF(4031,"商品已经下架"),
    DEAL_NOT_EXIST(4032,"订单不存在"),
    DEAL_ADD_FAIL(4035,"订单添加失败"),
    NOT_ACCEPTABLE(406,"参数不正确");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}