package com.erp.module.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pro_bom")
public class ProBom {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long materialId;

    private BigDecimal qtyPerUnit;

    private BigDecimal lossRate;

    private String unit;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}