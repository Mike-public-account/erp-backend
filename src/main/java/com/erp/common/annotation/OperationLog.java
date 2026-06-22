package com.erp.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    // 模块名称
    String module();
    // 操作描述
    String operation();
}