package com.erp.module.sales.dto;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class LogisticsReconcileDTO {
    @NotNull(message = "物流记录ID")
    private Long logisticsId;
    private String reconcileRemark;
}