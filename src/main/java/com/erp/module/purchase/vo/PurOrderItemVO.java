package com.erp.module.purchase.vo;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurOrderItemVO {
    private Long id;
    private Long orderId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal planQty;
    private BigDecimal arrivedQty;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private String remark;
    private String unit;
}