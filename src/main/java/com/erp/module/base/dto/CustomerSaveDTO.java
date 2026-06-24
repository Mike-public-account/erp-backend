package com.erp.module.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel("客户保存参数")
public class CustomerSaveDTO {

    @ApiModelProperty("物料主键id，新增传null，编辑传已有id")
    private Long id;
    @NotBlank(message = "客户编码不能为空")
    private String customerCode;
    @NotBlank(message = "客户名称不能为空")
    private String customerName;
    @ApiModelProperty("联系人")
    private String contactPerson;
    @ApiModelProperty("联系电话")
    private String contactPhone;
    @ApiModelProperty("收货地址")
    private String shippingAddress;
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    @ApiModelProperty("纬度")
    private BigDecimal latitude;
    @ApiModelProperty("授信额度")
    private BigDecimal creditLimit;
    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status = 1;
}