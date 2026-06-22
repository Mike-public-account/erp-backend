package com.erp.module.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inv_stock_record")
public class InvStockRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;

    private Long warehouseId;

    private Integer recordType;

    private Integer stockStatus;

    private BigDecimal qtyChange;

    private BigDecimal unitCost;

    private BigDecimal totalCost;

    private BigDecimal qtyAfter;

    private BigDecimal avgCostAfter;

    private String refType;

    private Long refId;

    private Long refItemId;

    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String remark;
}