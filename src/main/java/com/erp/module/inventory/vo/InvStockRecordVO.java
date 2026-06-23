package com.erp.module.inventory.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvStockRecordVO {
    private Long id;
    private Long materialId;
    private String materialName;
    private Long warehouseId;
    private String warehouseName;
    private Integer recordType;
    private String typeText;
    private Integer stockStatus;
    private String stockText;
    private BigDecimal qtyChange;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private BigDecimal qtyAfter;
    private BigDecimal avgCostAfter;
    private String refType;
    private Long refId;
    private Long refItemId;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
    private String remark;
}