package com.erp.module.production.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BomSaveDTO {
    private Long id;

    @NotNull(message = "成品物料不能为空")
    private Long productId;

    @NotNull(message = "原料物料不能为空")
    private Long materialId;

    @NotNull(message = "单件用料数量不能为空")
    private BigDecimal qtyPerUnit;

    private BigDecimal lossRate;

    private String unit;

    private String remark;
}