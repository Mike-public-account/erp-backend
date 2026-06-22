package com.erp.common.constant;

/**
 * 全局状态码常量
 */
public class GlobalConstant {
    // 请求成功
    public static final Integer SUCCESS = 200;
    // 参数错误
    public static final Integer PARAM_ERR = 400;
    // 未登录
    public static final Integer UNAUTHORIZED = 401;
    // 无操作权限
    public static final Integer FORBIDDEN = 403;
    // 服务异常
    public static final Integer SERVER_ERR = 500;

    // 逻辑删除
    public static final Integer NOT_DELETE = 0;
    public static final Integer DELETED = 1;
}