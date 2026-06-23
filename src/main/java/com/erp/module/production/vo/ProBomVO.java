package com.erp.module.production.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProBomVO {
    private Long id;
    private Long productId;
    private String productName;
    private Long materialId;
    private String materialName;
    private BigDecimal qtyPerUnit;
    private BigDecimal lossRate;
    private String unit;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}