package com.erp.module.production.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;

@Data
public class BomPageDTO {
    @Min(1)
    private Long pageNum = 1L;
    @Range(min = 1, max = 100)
    private Long pageSize = 10L;
    private Long productMaterialId;
    private String materialKeyword;
    private Long productId; // 存成品物料ID
    private Long materialId; // 存原料物料ID
}