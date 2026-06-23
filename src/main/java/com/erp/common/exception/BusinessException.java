package com.erp.common.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private Integer code;

    // 原有双参数构造
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    // 新增单参数封装，统一业务异常码 400
    public BusinessException(String message) {
        this(400, message);
    }
}