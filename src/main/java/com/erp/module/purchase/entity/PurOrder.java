package com.erp.module.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pur_order")
public class PurOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long supplierId;

    private Long warehouseId;

    private Integer orderStatus;

    private BigDecimal totalAmount;

    private LocalDateTime estimatedArrivalTime;

    private LocalDateTime timeoutTime;

    private LocalDateTime actualArrivalTime;

    private Long purchaserId;

    private Long approverId;

    private LocalDateTime approveTime;

    private String approveRemark;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}