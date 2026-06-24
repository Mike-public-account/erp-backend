package com.erp.module.purchase.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("采购单审批入参")
public class PurAuditDTO {

    @NotNull(message = "采购单id不能为空")
    @ApiModelProperty("采购单id")
    private Long orderId;

    @NotNull(message = "审批人id不能为空")
    @ApiModelProperty("审批人id")
    private Long auditorId;

    @NotNull(message = "审批状态不能为空 1通过 2驳回")
    @ApiModelProperty("审批状态 1通过 2驳回")
    private Integer auditStatus;

    @ApiModelProperty("审批意见")
    private String auditRemark;
}