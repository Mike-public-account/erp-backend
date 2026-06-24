package com.erp.module.finance.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class CostQueryDTO {
    private Long materialId;
    private String settlePeriod;
    private LocalDate startDate;
    private LocalDate endDate;
    @Min(value = 1, message = "页码最小为1")
    private Long pageNum = 1L;

    @Range(min = 1, max = 100, message = "每页条数1~100")
    private Long pageSize = 10L;
}