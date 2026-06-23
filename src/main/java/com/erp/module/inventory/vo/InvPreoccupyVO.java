package com.erp.module.inventory.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvPreoccupyVO {
    private Long id;
    private Long materialId;
    private String materialName;
    private Long warehouseId;
    private String warehouseName;
    private Integer preoccupyType;
    private String typeText;
    private BigDecimal qty;
    private Integer preoccupyStatus;
    private String statusText;
    private String refType;
    private Long refId;
    private LocalDateTime timeoutTime;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
}