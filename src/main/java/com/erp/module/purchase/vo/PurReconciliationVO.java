package com.erp.module.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurReconciliationVO {
    private Long id;
    /** 供应商名称 */
    private String supplierName;
    private Long supplierId;
    /** 对账周期 */
    private LocalDate startDate;
    private LocalDate endDate;
    /** 对账总金额 */
    private BigDecimal totalAmount;
    /** 抵扣预付款 */
    private BigDecimal deductPrepayAmount;
    /** 应付金额 */
    private BigDecimal payableAmount;
    /** 对账状态 1待确认 2已对账完成 */
    private Integer reconcileStatus;
    /** 对账确认人名称 */
    private String confirmUserName;
    private Long confirmUserId;
    private LocalDateTime confirmTime;
    private String remark;
    private LocalDateTime createTime;
}