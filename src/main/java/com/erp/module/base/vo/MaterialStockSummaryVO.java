package com.erp.module.base.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MaterialStockSummaryVO {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String spec;
    private String unit;
    private Integer materialType;

    private BigDecimal currentStock;
    private BigDecimal lockedStock;
    private BigDecimal availableStock;
    private BigDecimal safetyStock;
    private BigDecimal avgCost;

    private BigDecimal totalInQty;
    private BigDecimal totalOutQty;
}