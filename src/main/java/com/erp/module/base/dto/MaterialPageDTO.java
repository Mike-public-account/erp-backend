package com.erp.module.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("物料分页查询参数")
public class MaterialPageDTO {
    @NotNull(message = "页码不能为空")
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @NotNull(message = "每页条数不能为空")
    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;

    @ApiModelProperty("物料名称/编码（模糊）")
    private String keyword;

    @ApiModelProperty("物料类型 1原料 2半成品 3成品")
    private Integer materialType;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;
}