package com.erp.module.base.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BasSupplierVO {
    private Long id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String bankName;
    private String bankAccount;
    private String taxNo;
    private Integer status;
    private LocalDateTime createTime;
}