package com.erp.module.finance.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CostRecordVO {
    private Long id;
    private Integer settleType;
    private String settlePeriod;
    private Long materialId;
    private String materialName;
    private BigDecimal totalInputQty;
    private BigDecimal totalMaterialCost;
    private BigDecimal unitCost;
    private BigDecimal saleQty;
    private BigDecimal saleAmount;
    private BigDecimal grossProfit;
    private BigDecimal grossMargin;
    private LocalDateTime calcTime;
    private String remark;
}