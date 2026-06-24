package com.erp.module.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("物料保存参数")
public class MaterialSaveDTO {

    @ApiModelProperty("物料主键id，新增传null，编辑传已有id")
    private Long id;
    @NotBlank(message = "物料编码不能为空")
    @ApiModelProperty("物料编码")
    private String materialCode;

    @NotBlank(message = "物料名称不能为空")
    @ApiModelProperty("物料名称")
    private String materialName;

    @NotNull(message = "物料类型不能为空")
    @ApiModelProperty("物料类型 1原料 2半成品 3成品")
    private Integer materialType;

    @NotBlank(message = "计量单位不能为空")
    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("规格型号")
    private String spec;

    @ApiModelProperty("安全库存")
    private BigDecimal safetyStock;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status = 1;

    @ApiModelProperty("备注")
    private String remark;
}