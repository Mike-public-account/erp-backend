package com.erp.common.constant;

public interface CacheKey {
    String USER_TOKEN = "erp:user:token:";
    String USER_PERM = "erp:user:perm:";
    String MATERIAL_STOCK = "erp:material:stock:";
    String MATERIAL_INFO = "erp:material:info:";
    long TOKEN_EXPIRE = 7 * 24 * 3600;
    long PERM_EXPIRE = 3600;
    String STOCK_WARNING_CHANNEL = "channel:stock_warning";
}