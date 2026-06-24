package com.erp.module.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BomVO {
    // 主键ID
    private Long id;

    // 前端展示-成品物料
    private Long productMaterialId;
    private String productMaterialName;
    private String productMaterialCode;

    // 前端展示-原料物料
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialCode;

    // 业务用量
    private BigDecimal perUnitQty;
    private BigDecimal lossRate;
    private String remark;

    // 创建时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 底层数据库ID（内部逻辑使用）
    private Long productId;
    private Long materialId;
}