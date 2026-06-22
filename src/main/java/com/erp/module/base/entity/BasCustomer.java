package com.erp.module.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bas_customer")
public class BasCustomer {
    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}