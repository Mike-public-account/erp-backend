package com.erp.module.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fin_purchase_suggestion")
public class FinPurchaseSuggestion {
    public void test() {
        this.getCurrentStock();
    }
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;

    private BigDecimal currentStock;

    private BigDecimal safetyStock;

    private BigDecimal avgWeeklyConsumption;

    private BigDecimal suggestedQty;

    private Integer urgencyLevel;

    private Long supplierId;

    private BigDecimal refPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime calcTime;
}