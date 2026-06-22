package com.erp.module.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bas_supplier")
public class BasSupplier {
    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}