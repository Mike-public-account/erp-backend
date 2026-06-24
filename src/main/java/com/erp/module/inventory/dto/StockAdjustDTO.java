package com.erp.module.inventory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 库存盘点调整入参
 */
@Data
public class StockAdjustDTO {
    /** 物料ID */
    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    /** 仓库ID */
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    /** 调整后实际库存 */
    @NotNull(message = "调整后库存不能为空")
    private BigDecimal newStock;

    /** 调整原因 */
    @NotBlank(message = "调整原因不能为空")
    private String adjustRemark;
}