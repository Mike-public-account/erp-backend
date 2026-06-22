package com.erp.common.annotation;

import java.lang.annotation.*;

/**
 * 接口权限校验注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    // 权限码 system:user:add
    String value();
}