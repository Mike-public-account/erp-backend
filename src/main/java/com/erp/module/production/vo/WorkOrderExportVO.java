package com.erp.module.production.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WorkOrderExportVO {
    private String workOrderNo;
    private String productName;
    private String productCode;
    private BigDecimal planQty;
    private BigDecimal actualQty;
    private BigDecimal unitMaterialCost;
    private BigDecimal totalMaterialCost;
    private Integer orderStatus;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private LocalDateTime actualEndTime;
    private String creatorName;
    private String remark;
}