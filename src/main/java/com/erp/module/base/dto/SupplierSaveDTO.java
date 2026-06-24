package com.erp.module.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel("供应商保存参数")
public class SupplierSaveDTO {
    @NotBlank(message = "供应商编码不能为空")
    private String supplierCode;
    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;
    @ApiModelProperty("联系人")
    private String contactPerson;
    @ApiModelProperty("联系电话")
    private String contactPhone;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    @ApiModelProperty("纬度")
    private BigDecimal latitude;
    @ApiModelProperty("开户银行")
    private String bankName;
    @ApiModelProperty("银行账号")
    private String bankAccount;
    @ApiModelProperty("税号")
    private String taxNo;
    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status = 1;
}