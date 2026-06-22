package com.erp.common.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 采购PO、销售SO、生产WO单号工具
 */
public class OrderNoUtil {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    /**
     * 生成单据号
     * @param prefix 前缀 PO/SO/WO
     */
    public static String generateNo(String prefix) {
        String date = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);
        int num = SEQ.getAndIncrement();
        if (num > 9999) {
            SEQ.set(1);
        }
        return prefix + date + String.format("%04d", num);
    }
}