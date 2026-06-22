package com.erp.common.exception;

import com.erp.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.error("业务异常:{}", e.getMessage(), e);
        return R.fail(e.getCode(), e.getMessage());
    }

    // 参数校验异常
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.error("参数校验异常:{}", msg);
        return R.fail(400, msg);
    }

    // 数据库唯一索引冲突
    @ExceptionHandler(DuplicateKeyException.class)
    public R<?> handleDuplicateKeyException() {
        return R.fail("数据重复，请勿重复提交");
    }

    // 全局未知异常兜底
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统未知异常", e);
        return R.fail(500, "服务器内部错误，请联系管理员");
    }
}