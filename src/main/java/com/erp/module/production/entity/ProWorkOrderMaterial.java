package com.erp.module.production.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("pro_work_order_material")
public class ProWorkOrderMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workOrderId;

    private Long materialId;

    private BigDecimal planQty;

    private BigDecimal actualQty;

    private BigDecimal unitCost;

    private BigDecimal totalCost;

    private Integer status;
}