package com.erp.module.production.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
public class WorkOrderPageDTO {
    @Min(value = 1, message = "页码最小为1")
    private Long pageNum = 1L;
    @Range(min = 1, max = 100, message = "每页条数范围1~100")
    private Long pageSize = 10L;
    private String workOrderNo;
    // 修正：和实体productId对应
    private Long productId;
    private Integer orderStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}