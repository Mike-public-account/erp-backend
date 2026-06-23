package com.erp.module.finance.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinPurchaseSuggestionVO {
    private Long id;
    private Long materialId;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal safetyStock;
    private BigDecimal avgWeeklyConsumption;
    private BigDecimal suggestedQty;
    private Integer urgencyLevel;
    private String levelText;
    private Long supplierId;
    private String supplierName;
    private BigDecimal refPrice;
    private LocalDateTime calcTime;
}