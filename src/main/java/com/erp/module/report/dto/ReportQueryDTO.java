package com.erp.module.report.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReportQueryDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long supplierId;
    private Long materialId;
    private Long customerId;
}