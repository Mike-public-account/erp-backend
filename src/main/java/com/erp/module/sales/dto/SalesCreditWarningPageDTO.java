package com.erp.module.sales.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 客户账期预警分页查询
 */
@Data
public class SalesCreditWarningPageDTO {
    /** 客户ID */
    private Long customerId;
    /** 超账期起始日期 */
    private LocalDate startDate;
    /** 超账期结束日期 */
    private LocalDate endDate;

    private Long pageNum = 1L;
    private Long pageSize = 10L;
}