package com.erp.module.inventory.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Data
public class InventoryBatchAdjustDTO {
    @NotEmpty(message = "盘点明细不能为空")
    private List<AdjustItemDTO> itemList;

    private String adjustRemark;

    @Data
    public static class AdjustItemDTO {
        private Long materialId;
        private Long warehouseId;
        private BigDecimal realStock; // 实际盘点数量
        private BigDecimal adjustQty;  // 调整差值（正增加/负减少）
        private String remark;
    }
}