package com.erp.module.report.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesSummaryVO {
    // 销售总金额
    private BigDecimal totalSaleAmount;
    // 总毛利
    private BigDecimal totalGrossProfit;
    // 整体毛利率（百分比）
    private BigDecimal totalGrossMargin;
    private List<Item> list;

    @Data
    public static class Item {
        // 分组名称（客户名称）
        private String groupName;
        // 销售总数量
        private BigDecimal saleQty;
        // 销售总金额
        private BigDecimal saleAmount;
        // 销售总成本
        private BigDecimal saleCost;
        // 毛利
        private BigDecimal grossProfit;
        // 毛利率 %
        private BigDecimal grossMargin;
    }
}