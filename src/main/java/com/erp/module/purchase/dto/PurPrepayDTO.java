package com.erp.module.purchase.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 采购预付款新增入参
 */
@Data
public class PurPrepayDTO {
    /**
     * 关联采购单ID
     */
    @NotNull(message = "采购单ID不能为空")
    private Long orderId;

    /**
     * 预付款金额
     */
    @NotNull(message = "预付款金额不能为空")
    private BigDecimal prepayAmount;

    /**
     * 付款备注
     */
    private String remark;
}