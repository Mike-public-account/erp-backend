package com.erp.module.production.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WorkOrderSaveDTO {
    private Long id;
    @NotNull(message = "成品物料不能为空")
    private Long productId;
    @NotNull(message = "计划生产数量不能为空")
    private BigDecimal planQty;
    @NotNull(message = "入库仓库不能为空")
    private Long warehouseId;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private String remark;
}