package com.erp.module.sales.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LogisticsVO {
    private Long id;
    private String logisticsCompany;
    private String trackingNo;
    private BigDecimal freightAmount;
    private BigDecimal estimatedFreight;
    private Integer freightStatus;
    private String freightText;
    private LocalDateTime shipTime;
    private LocalDateTime expectedArriveTime;
    private LocalDateTime actualArriveTime;
    private BigDecimal distanceKm;
    private String reconcileRemark;
}