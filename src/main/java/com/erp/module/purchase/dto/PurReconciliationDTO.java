package com.erp.module.purchase.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurReconciliationDTO {
    /** 供应商ID */
    @NotNull(message = "供应商不能为空")
    private Long supplierId;
    /** 对账开始日期 */
    @NotNull(message = "对账开始日期不能为空")
    private LocalDate startDate;
    /** 对账结束日期 */
    @NotNull(message = "对账结束日期不能为空")
    private LocalDate endDate;
    /** 本次对账总金额 */
    private BigDecimal totalAmount;
    /** 本次抵扣预付款金额 */
    private BigDecimal deductPrepayAmount;
    /** 应付剩余金额 */
    private BigDecimal payableAmount;
    /** 对账备注 */
    private String remark;
    /** 关联采购单ID集合 */
    private List<Long> orderIdList;
}