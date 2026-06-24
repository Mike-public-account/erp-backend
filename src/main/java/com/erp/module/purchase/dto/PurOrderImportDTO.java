package com.erp.module.purchase.dto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PurOrderImportDTO {
    private String orderNo;
    private Long materialId;
    private BigDecimal planQty;
    private BigDecimal unitPrice;
    private String remark;
    /** 目标仓库ID */
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    /** 供应商纬度 */
    @NotNull(message = "供应商纬度不能为空")
    private Double supplierLat;

/** 供应商经度 */
    @NotNull(message = "供应商经度不能为空")
    private Double supplierLng;
}