package com.erp.module.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pro_work_order")
public class ProWorkOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String workOrderNo;

    private Long productId;

    private BigDecimal planQty;

    private BigDecimal actualQty;

    private Integer orderStatus;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;

    private LocalDateTime actualEndTime;

    private Long warehouseId;

    private Long creatorId;

    private BigDecimal totalMaterialCost;

    private BigDecimal unitMaterialCost;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    // 新增逻辑删除字段
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}