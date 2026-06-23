package com.erp.module.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BomVO {
    private Long id;
    private Long productMaterialId;
    private String productMaterialName;
    private String productMaterialCode;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialCode;
    private BigDecimal perUnitQty;
    private BigDecimal lossRate;
    private String remark;
    private LocalDateTime createTime;
}