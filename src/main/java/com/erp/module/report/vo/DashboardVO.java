package com.erp.module.report.vo;

import lombok.Data;

@Data
public class DashboardVO {
    // 今日采购单数量
    private Long todayPurchaseCount;
    // 今日销售单数量
    private Long todaySalesCount;
    // 在途采购（待入库）
    private Long transitPurchase;
    // 库存预警物料数
    private Long warningMaterialCount;
}