package com.susstore.result;

/**
 *
 *  4001:用户未登录
 *  4002:登陆失败
 *  4010:注册失败
 *
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
    REGISTER_FAIL(4010,"注册失败"),
    LOGIN_FAIL(4002,"登录失败");
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