package com.erp.module.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("客户分页查询参数")
public class CustomerPageDTO {
    @NotNull(message = "页码不能为空")
    private Integer pageNum = 1;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize = 10;
    @ApiModelProperty("客户名称/编码（模糊）")
    private String keyword;
    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}