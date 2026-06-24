package com.erp.module.report.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseSummaryVO {
    private BigDecimal totalAmount;
    private List<Item> list;

    @Data
    public static class Item {
        // 分组名称（供应商名称）
        private String groupName;
        // 采购总数量
        private BigDecimal qty;
        // 采购总金额
        private BigDecimal amount;
    }
}