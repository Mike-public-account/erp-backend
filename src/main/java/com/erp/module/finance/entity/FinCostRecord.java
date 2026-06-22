package com.erp.module.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fin_cost_record")
public class FinCostRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer settleType;

    private String settlePeriod;

    private Long materialId;

    private BigDecimal totalInputQty;

    private BigDecimal totalMaterialCost;

    private BigDecimal unitCost;

    private BigDecimal saleQty;

    private BigDecimal saleAmount;

    private BigDecimal grossProfit;

    private BigDecimal grossMargin;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime calcTime;

    private String remark;
}