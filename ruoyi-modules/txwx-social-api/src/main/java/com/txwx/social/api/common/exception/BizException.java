/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.api.common.exception;

import com.txwx.social.api.common.exception.enums.ErrorCode;

/**
 * 业务异常类
 * 用于明确区分业务逻辑错误与系统异常
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 附加数据（可选）
     */
    private Object data;


    public BizException(ErrorCode errorCode) {
        this.message = errorCode.getMsg();
        this.code = errorCode.getCode();
    }

    public BizException(String message) {
        this.message = message;
    }

    public BizException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public BizException(String message, Integer code, Object data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    // 使用预定义的错误码（推荐）
    public BizException(String message, ErrorCode errorCode) {
        this.message = message;
        this.code = errorCode.getCode();
    }

    public BizException(String message, ErrorCode errorCode, Object data) {
        this.message = message;
        this.code = errorCode.getCode();
        this.data = data;
    }

    // getter 方法
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
