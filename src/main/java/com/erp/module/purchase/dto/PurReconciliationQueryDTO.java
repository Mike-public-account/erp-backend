package com.erp.module.purchase.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PurReconciliationQueryDTO {
    /** 供应商ID */
    private Long supplierId;
    /** 对账状态 1待确认 2已完成 */
    private Integer reconcileStatus;
    /** 对账日期起始 */
    private LocalDate startDate;
    /** 对账日期结束 */
    private LocalDate endDate;
    /** 分页页码 */
    private Long pageNum = 1L;
    /** 每页条数 */
    private Long pageSize = 10L;
}