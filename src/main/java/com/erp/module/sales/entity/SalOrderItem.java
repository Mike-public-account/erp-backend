package com.erp.module.sales.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("sal_order_item")
public class SalOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long materialId;

    private BigDecimal planQty;

    private BigDecimal shippedQty;

    private BigDecimal unitPrice;

    private BigDecimal unitCost;

    private BigDecimal amount;

    private BigDecimal taxRate;

    private String remark;
}