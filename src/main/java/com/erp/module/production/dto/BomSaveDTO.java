package com.erp.module.production.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BomSaveDTO {
    private Long id;
    @NotNull(message = "成品物料不能为空")
    private Long productMaterialId;
    @NotNull(message = "原料物料不能为空")
    private Long rawMaterialId;
    @NotNull(message = "单件用料数量不能为空")
    private BigDecimal perUnitQty;
    private BigDecimal lossRate;
    private String remark;
    private Long productId; // 存成品物料ID
    private Long materialId; // 存原料物料ID
}