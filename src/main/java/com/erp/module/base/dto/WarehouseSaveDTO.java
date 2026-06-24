package com.erp.module.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel("仓库保存参数")
public class WarehouseSaveDTO {
    @NotBlank(message = "仓库编码不能为空")
    private String warehouseCode;
    @NotBlank(message = "仓库名称不能为空")
    private String warehouseName;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    @ApiModelProperty("纬度")
    private BigDecimal latitude;
    @ApiModelProperty("负责人ID")
    private Long managerId;
    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status = 1;
}