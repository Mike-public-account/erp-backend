package com.erp.module.sales.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 销售单明细入参DTO
 */
@Data
public class SalesOrderItemDTO {

    /** 物料ID */
    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    /** 销售出库数量 */
    @NotNull(message = "销售数量不能为空")
    private BigDecimal saleQty;

    /** 销售单价 */
    @NotNull(message = "销售单价不能为空")
    private BigDecimal salePrice;

    /** 折扣单价（可选） */
    private BigDecimal discountPrice;

    /** 行备注 */
    private String itemRemark;
}