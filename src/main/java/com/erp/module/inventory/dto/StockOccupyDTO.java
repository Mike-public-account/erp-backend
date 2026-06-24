package com.erp.module.inventory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存预占入参DTO
 */
@Data
public class StockOccupyDTO {
    /** 物料ID */
    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    /** 仓库ID */
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    /** 预占数量 */
    @NotNull(message = "预占数量不能为空")
    private BigDecimal qty;

    /** 预占类型 1采购预占 2生产预出库 3销售预出库 */
    @NotNull(message = "预占类型不能为空")
    private Integer preoccupyType;

    /** 关联单据类型 PUR_ORDER / PROD_ORDER / SALE_ORDER */
    @NotNull(message = "单据类型不能为空")
    private String refType;

    /** 关联单据ID */
    @NotNull(message = "单据ID不能为空")
    private Long refId;

    /** 超时释放时间（采购预占专用，生产/销售传null） */
    private LocalDateTime timeoutTime;

    /** 操作人ID */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
}