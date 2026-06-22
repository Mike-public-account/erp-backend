package com.erp.module.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 库存预占DTO
 */
@Data
public class StockOccupyDTO {
    /** 物料ID */
    private Long materialId;
    /** 仓库ID */
    private Long warehouseId;
    /** 预占数量 */
    private BigDecimal occupyQty;
    /** 单据类型 PO/SO/WO */
    private String refType;
    /** 单据ID */
    private Long refId;
}