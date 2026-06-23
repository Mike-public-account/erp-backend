package com.erp.module.production.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WorkOrderFinishDTO {
    @NotNull(message = "工单ID不能为空")
    private Long workOrderId;
    @NotNull(message = "完工入库数量不能为空")
    private BigDecimal actualQty;
}