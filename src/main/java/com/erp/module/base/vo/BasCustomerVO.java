package com.erp.module.base.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BasCustomerVO {
    private Long id;
    private String customerCode;
    private String customerName;
    private String contactPerson;
    private String contactPhone;
    private String shippingAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal creditLimit;
    private Integer status;
    private LocalDateTime createTime;
}