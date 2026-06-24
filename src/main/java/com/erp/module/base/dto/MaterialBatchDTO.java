package com.erp.module.base.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MaterialBatchDTO {
    @NotEmpty(message = "物料数据不能为空")
    private List<MaterialSaveDTO> list;
}