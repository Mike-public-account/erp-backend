package com.erp.module.sales.vo;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalOrderItemVO {
    private Long id;
    private Long orderId;
    private Long materialId;
    private String materialName;
    private BigDecimal planQty;
    private BigDecimal shippedQty;
    private BigDecimal unitPrice;
    private BigDecimal unitCost;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private String remark;
}