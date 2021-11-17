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

    SUCCESS(2000,"成功"),
    QUERY_NOT_LOGIN_USER(2001,"查询用户不是登录用户"),
    QUERY_IS_LOGIN_USER(2002,"查询用户是登录用户"),
    CHECK_CODE_WRONG(4000,"验证码出错"),
    PARAM_NOT_VALID(4001,"填写的参数有误"),
    ACCESS_DENIED(4003,"权限不足不允许访问"),
    USER_NOT_LOGIN(4010, "用户未登录"),
    USER_NOT_ACTIVATE(4011, "用户未激活"),
    USER_NOT_FOUND(4012, "用户不存在"),
    USER_BANNED(4014,"用户封禁中"),
    LOGIN_FAIL(4013,"用户名或密码错误"),
    EMAIL_EXIST(4020, "注册邮箱已存在"),
    EMAIL_NOT_FOUND(4021, "邮箱不存在"),
    ACTIVATE_CODE_ILLEGAL(4022, "激活码不存在"),
    USER_ALREADY_ACTIVATE(4023, "用户已经激活"),
    ADDRESS_NOT_EXISTS(4030, "用户地址不存在"),
    COLLECTION_EXISTS(4040,"收藏夹已有"),
    COLLECTION_NOT_EXISTS(4041,"收藏夹未有"),
    GOODS_NOT_FOUND(4050, "商品不存在"),
    GOODS_OFF_SHELL(4051, "商品下架"),
    ADD_GOODS_FAILED(4060, "添加商品失败"),
    DEAL_NOT_EXISTS(4070, "订单不存在"),
    DEAL_ADD_FAIL(4071, "添加订单失败"),
    STAGE_WRONG(4072, "订单不能跨越阶段"),
    NOT_ENOUGH_MONEY(4073, "没有足够的钱"),
    ALREADY_COMMENT(4074, "已经评价"),
    CHAT_ALREADY_EXISTS(4090, "聊天已存在"),
    COMPLAIN_FAIL(4100, "投诉失败"),
    COMPLAIN_USER_NOT_EXISTS(4101, "举报用户不存在");
    public int code;
    public String message;

    ResultCode(int code, String message) {
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