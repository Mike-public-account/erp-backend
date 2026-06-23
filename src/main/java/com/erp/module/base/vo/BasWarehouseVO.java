package com.erp.module.base.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BasWarehouseVO {
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String address;
    private Long managerId;
    private String managerName; // 扩展负责人姓名
    private Integer status;
    private LocalDateTime createTime;
}