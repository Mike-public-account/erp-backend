package com.erp.module.purchase.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
public class PurchasePageDTO {
    @Min(value = 1, message = "页码最小为1")
    private Long pageNum = 1L;

    @Range(min = 1, max = 100, message = "每页条数范围1~100")
    private Long pageSize = 10L;

    /** 采购单号模糊查询 */
    private String orderNo;

    /** 供应商id筛选 */
    private Long supplierId;

    /** 单据状态 */
    private Integer orderStatus;

    /** 创建时间范围-开始 */
    private LocalDateTime startTime;

    /** 创建时间范围-结束 */
    private LocalDateTime endTime;
}