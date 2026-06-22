package com.erp.module.sales.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sal_order")
public class SalOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long customerId;

    private Long warehouseId;

    private Integer orderStatus;

    private Integer paymentStatus;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal actualAmount;

    private Long salesmanId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}