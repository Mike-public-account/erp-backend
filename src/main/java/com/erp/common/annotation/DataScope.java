package com.erp.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解，标记接口需要自动拼接数据过滤SQL
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    // 表别名，如 o、p
    String alias() default "";
    // 字段：创建人ID字段
    String userIdColumn() default "creator_id";
    // 是否超级管理员无视数据权限
    boolean adminIgnore() default true;
}