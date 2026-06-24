package com.erp.module.inventory.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockWarningMsgVO {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal lockedStock;
    private BigDecimal availableStock;
    private BigDecimal safetyStock;
}