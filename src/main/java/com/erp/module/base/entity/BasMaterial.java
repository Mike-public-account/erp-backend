package com.erp.module.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bas_material")
public class BasMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String materialCode;

    private String materialName;

    private Integer materialType;

    private String spec;

    private String unit;

    private Long categoryId;

    private BigDecimal currentStock;

    private BigDecimal lockedStock;

    private BigDecimal safetyStock;

    private BigDecimal avgCost;

    private BigDecimal lastPurchasePrice;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}