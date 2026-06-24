package com.erp.module.sales.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 销售单分页、导出、账期预警查询条件
 */
@Data
public class SalesPageDTO {
    // 客户筛选
    private Long customerId;
    // 订单状态
    private Integer orderStatus;
    // 下单日期区间
    private LocalDate startDate;
    private LocalDate endDate;
    // 账期预警专用：是否只查超账期
    private Boolean onlyOverdue;

    // 分页参数
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}