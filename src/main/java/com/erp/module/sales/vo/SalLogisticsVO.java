package com.erp.module.sales.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SalLogisticsVO {
    private Long id;
    private Long orderId;
    private String orderNo;
    private String logisticsCompany;
    private String trackingNo;
    private BigDecimal freightAmount;
    private BigDecimal estimatedFreight;
    private Integer freightStatus;
    private String statusText;
    private LocalDateTime shipTime;
    private LocalDateTime expectedArriveTime;
    private LocalDateTime actualArriveTime;
    private BigDecimal distanceKm;
    private String reconcileRemark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
}