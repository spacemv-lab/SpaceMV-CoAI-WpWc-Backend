package com.txwx.social.api.common.exception.enums;

/**
 * 业务错误码枚举
 * 可按模块划分，如：用户相关 1001~1999
 */
public enum ErrorCode {

    // 通用业务错误
    PARAMS_ERROR(1001, "参数校验失败"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    OPERATION_NOT_ALLOWED(1003, "不允许的操作"),
    BUSINESS_LOGIC_ERROR(1004, "业务逻辑错误"),

    // 用户模块
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_DISABLED(2002, "用户已禁用");



    private final Integer code;
    private final String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
