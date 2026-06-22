package com.erp.module.inventory.service;

import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.entity.InvStockRecord;
import java.math.BigDecimal;

public interface StockService {
    /**
     * 库存预占（采购/生产/下单）
     */
    void preOccupy(StockOccupyDTO dto);

    /**
     * 释放预占（取消单据、超时）
     */
    void releaseOccupy(InvPreoccupy record);

    /**
     * 采购入库，计算加权平均成本
     */
    void purchaseIn(Long materialId, Long warehouseId, BigDecimal inQty, BigDecimal unitPrice, String refType, Long refId);

    /**
     * 物料出库（生产领料/销售发货）
     */
    void stockOut(Long materialId, Long warehouseId, BigDecimal outQty, String refType, Long refId);
}