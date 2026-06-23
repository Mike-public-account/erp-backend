package com.erp.module.purchase.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurOrderVO {
    private Long id;
    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private Long warehouseId;
    private String warehouseName;
    private Integer orderStatus;
    private String statusText; // 状态中文
    private BigDecimal totalAmount;
    private LocalDateTime estimatedArrivalTime;
    private LocalDateTime timeoutTime;
    private LocalDateTime actualArrivalTime;
    private Long purchaserId;
    private String purchaserName;
    private Long approverId;
    private String approveName;
    private LocalDateTime approveTime;
    private String approveRemark;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<PurOrderItemVO> itemList; // 关联明细
}