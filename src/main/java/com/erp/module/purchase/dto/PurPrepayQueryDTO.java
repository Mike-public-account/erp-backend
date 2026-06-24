package com.erp.module.purchase.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PurPrepayQueryDTO {
    /** 供应商ID */
    private Long supplierId;
    /** 采购单ID */
    private Long orderId;
    /** 预付款状态 0未核销 1已部分核销 2全部核销 */
    private Integer prepayStatus;
    /** 付款日期区间 */
    private LocalDate startDate;
    private LocalDate endDate;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}