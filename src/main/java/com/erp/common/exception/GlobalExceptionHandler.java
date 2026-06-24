package com.erp.common.exception;

import com.erp.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务自定义异常
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    // @RequestBody JSON DTO校验失败
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        log.error("JSON参数校验失败：{}", msg);
        return R.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    // @RequestParam / @PathVariable 单参数校验
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        log.error("路径/表单参数校验失败：{}", msg);
        return R.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    // 表单提交 BindException
    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        log.error("表单参数校验失败：{}", msg);
        return R.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    // 全局未知异常兜底
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统未知异常", e);
        return R.fail(500, "服务器内部错误，请联系管理员");
    }
}