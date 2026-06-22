package com.erp.module.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inv_preoccupy")
public class InvPreoccupy {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;

    private Long warehouseId;

    private Integer preoccupyType;

    private BigDecimal qty;

    private Integer preoccupyStatus;

    private String refType;

    private Long refId;

    private LocalDateTime timeoutTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime releaseTime;
}