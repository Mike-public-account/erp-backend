package com.erp.module.sales.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sal_logistics")
public class SalLogistics {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String logisticsCompany;

    private String trackingNo;

    private BigDecimal freightAmount;

    private BigDecimal estimatedFreight;

    private Integer freightStatus;

    private LocalDateTime shipTime;

    private LocalDateTime expectedArriveTime;

    private LocalDateTime actualArriveTime;

    private BigDecimal distanceKm;

    private String reconcileRemark;

    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}