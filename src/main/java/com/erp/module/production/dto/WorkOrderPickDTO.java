package com.erp.module.production.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WorkOrderPickDTO {
    @NotNull(message = "工单ID不能为空")
    private Long workOrderId;

    private List<PickItem> itemList;

    @Data
    public static class PickItem {
        /** 原料物料ID */
        private Long rawMaterialId;
        /** 本次实际领料数量 */
        private BigDecimal pickQty;
    }
}