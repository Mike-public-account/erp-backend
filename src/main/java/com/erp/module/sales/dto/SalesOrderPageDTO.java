package com.erp.module.sales.dto;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
public class SalesOrderPageDTO {
    @Min(1)
    private Long pageNum = 1L;
    @Range(min = 1, max = 100)
    private Long pageSize = 10L;
    private String orderNo;
    private Long customerId;
    private Integer orderStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}