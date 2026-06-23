package com.erp.module.base.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BasMaterialVO {
    private Long id;
    private String materialCode;
    private String materialName;
    private Integer materialType;
    private String spec;
    private String unit;
    private Long categoryId;
    private String categoryName; // 扩展分类名
    private BigDecimal currentStock;
    private BigDecimal lockedStock;
    private BigDecimal availableStock;
    private BigDecimal safetyStock;
    private BigDecimal avgCost;
    private BigDecimal lastPurchasePrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 剔除 isDeleted
}