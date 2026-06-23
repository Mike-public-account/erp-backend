package com.erp.common.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 采购PO、销售SO、生产WO单号工具
 */
public class OrderNoUtil {
    // 每日重置序列，避免跨日序号累积过大
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private static String CURRENT_DATE = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);

    /**
     * 生成单据号
     * @param prefix 前缀 PO/SO/WO
     * @return 单据号 例：WO202606230001
     */
    public static String generate(String prefix) {
        String today = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);
        // 日期变化则重置序号
        if (!today.equals(CURRENT_DATE)) {
            synchronized (OrderNoUtil.class) {
                if (!today.equals(CURRENT_DATE)) {
                    SEQ.set(1);
                    CURRENT_DATE = today;
                }
            }
        }
        int num = SEQ.getAndIncrement();
        if (num > 9999) {
            SEQ.set(1);
            num = SEQ.getAndIncrement();
        }
        return prefix + today + String.format("%04d", num);
    }
}