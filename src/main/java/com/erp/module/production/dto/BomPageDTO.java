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

    // 模糊搜索物料名称/编码，不属于实体业务字段，保留用于查询
    private String materialKeyword;

    // 和实体同名：成品物料ID
    private Long productId;

    // 和实体同名：原料子件物料ID
    private Long materialId;
}