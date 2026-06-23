package com.erp.module.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkOrderVO {
    private Long id;
    private String workOrderNo;
    private Long productMaterialId;
    private String productMaterialName;
    private String productMaterialCode;
    private BigDecimal planQty;
    private BigDecimal finishQty;
    private Integer orderStatus;
    private String statusText;
    private Long warehouseId;
    private String warehouseName;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private LocalDateTime actualFinishTime;
    private BigDecimal totalMaterialCost;
    private String remark;
    private LocalDateTime createTime;
    private List<WorkOrderMaterialVO> materialList;
}