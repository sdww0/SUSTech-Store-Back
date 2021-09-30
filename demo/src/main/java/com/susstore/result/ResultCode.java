package com.susstore.result;

/**
 *  4001:用户未登录
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
    USER_NOT_LOGIN(4001,"用户未登录");
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