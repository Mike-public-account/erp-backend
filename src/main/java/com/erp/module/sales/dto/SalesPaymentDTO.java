package com.erp.module.sales.dto;

import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 销售单收款核销入参
 */
@Data
public class SalesPaymentDTO {
    /** 销售单ID */
    @NotNull(message = "销售单ID不能为空")
    private Long orderId;

    /** 本次收款金额 */
    @NotNull(message = "收款金额不能为空")
    @DecimalMin(value = "0.01", message = "收款金额必须大于0")
    private BigDecimal payAmount;

    /** 收款备注 */
    private String remark;
}