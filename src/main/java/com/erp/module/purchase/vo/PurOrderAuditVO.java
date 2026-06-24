package com.erp.module.purchase.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel("采购单审批记录VO")
public class PurOrderAuditVO {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("采购单id")
    private Long orderId;

    @ApiModelProperty("审批人id")
    private Long auditorId;

    @ApiModelProperty("审批人姓名")
    private String auditorName;

    @ApiModelProperty("审批状态 1通过 2驳回")
    private Integer auditStatus;

    @ApiModelProperty("审批意见")
    private String auditRemark;

    @ApiModelProperty("审批时间")
    private LocalDateTime auditTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}