package com.erp.module.inventory.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 库存列表查询条件
 */
@Data
public class StockQueryDTO {
    /** 页码 */
    @Min(value = 1, message = "页码不能小于1")
    private Long pageNum;

    /** 每页条数 */
    @Min(value = 1, message = "每页条数不能小于1")
    private Long pageSize;

    /** 物料名称/编码 模糊搜索 */
    private String keyword;

    /** 物料类型 1原料 2半成品 3成品 */
    private Integer materialType;

    /** 分类ID */
    private Long categoryId;

    /** 是否只查库存预警（1是 0否） */
    private Integer warnFlag;
}