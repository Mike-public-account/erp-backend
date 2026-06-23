package com.erp.module.production.vo;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProWorkOrderMaterialVO {
    private Long id;
    private Long workOrderId;
    private Long materialId;
    private String materialName;
    private BigDecimal planQty;
    private BigDecimal actualQty;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private Integer status;
}