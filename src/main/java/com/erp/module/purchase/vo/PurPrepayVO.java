package com.erp.module.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurPrepayVO {
    private Long id;
    /** 采购单号 */
    private String orderNo;
    private Long orderId;
    /** 供应商名称 */
    private String supplierName;
    private Long supplierId;
    /** 预付款总额 */
    private BigDecimal totalPrepayAmount;
    /** 已核销金额 */
    private BigDecimal writeOffAmount;
    /** 剩余可抵扣金额 */
    private BigDecimal remainAmount;
    /** 预付款状态 0未核销 1部分核销 2全部核销 */
    private Integer prepayStatus;
    private String remark;
    private LocalDateTime createTime;
}