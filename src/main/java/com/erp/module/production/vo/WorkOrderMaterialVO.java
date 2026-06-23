package com.erp.module.production.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WorkOrderMaterialVO {
    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialCode;
    private BigDecimal requireQty;
    private BigDecimal pickQty;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private Integer pickStatus;
    private String pickStatusText;
}