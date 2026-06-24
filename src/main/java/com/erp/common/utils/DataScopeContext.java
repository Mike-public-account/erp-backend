package com.erp.common.utils;

/**
 * 数据权限线程上下文，存储当前登录用户ID、是否超级管理员
 * 使用ThreadLocal隔离多请求数据，用完必须clear防止线程池污染
 */
public class DataScopeContext {

    // 当前登录用户ID
    private static final ThreadLocal<Long> USER_ID_LOCAL = new ThreadLocal<>();
    // 是否超级管理员
    private static final ThreadLocal<Boolean> IS_ADMIN_LOCAL = new ThreadLocal<>();

    /** 设置登录用户ID */
    public static void setUserId(Long userId) {
        USER_ID_LOCAL.set(userId);
    }

    /** 获取登录用户ID */
    public static Long getUserId() {
        return USER_ID_LOCAL.get();
    }

    /** 设置是否超管 */
    public static void setAdmin(boolean admin) {
        IS_ADMIN_LOCAL.set(admin);
    }

    /** 判断是否超管 */
    public static boolean isAdmin() {
        Boolean flag = IS_ADMIN_LOCAL.get();
        return flag != null && flag;
    }

    /**
     * 清除本地线程变量
     * 放在finally执行，避免线程池复用产生脏数据
     */
    public static void clear() {
        USER_ID_LOCAL.remove();
        IS_ADMIN_LOCAL.remove();
    }
}