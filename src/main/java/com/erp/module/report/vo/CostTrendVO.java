package com.erp.module.report.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CostTrendVO {
    private List<Item> trendList;

    @Data
    public static class Item {
        private String period; // 周期 2026-W24
        private BigDecimal unitCost;
        private BigDecimal totalCost;
    }
}