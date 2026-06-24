package com.erp.module.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.inventory.dto.InventoryBatchAdjustDTO;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.dto.StockAdjustDTO;
import com.erp.module.inventory.dto.StockQueryDTO;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.entity.InvStockRecord;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface StockService {
    /**
     * 库存预占（采购/生产/下单）
     */
    void preOccupy(StockOccupyDTO dto);

    /**
     * 释放预占（单条记录）
     */
    void releaseOccupy(InvPreoccupy record);

    /**
     * 根据单据refId批量释放该单据全部预占记录
     */
    void releasePreoccupyByRef(Long refId, String refType);

    /**
     * 采购入库，计算加权平均成本
     */
    void purchaseIn(Long materialId, Long warehouseId, BigDecimal inQty,
                    BigDecimal unitPrice, String refType, Long refId,
                    Long refItemId, Long operatorId);

    /**
     * 物料出库（生产领料/销售发货）
     */
    void stockOut(Long materialId, Long warehouseId, BigDecimal outQty,
                  String refType, Long refId, Long operatorId);

    // 新增缺失的分页、流水、预警、盘点方法
    IPage<BasMaterial> pageStock(Page<BasMaterial> page, StockQueryDTO dto);

    List<InvStockRecord> getRecordByMaterialId(Long materialId);

    List<BasMaterial> getWarningMaterial();

    List<InvPreoccupy> getAllPreoccupy();

    void adjustStock(StockAdjustDTO dto);
    // 接口末尾新增，原有方法全部保留

    /**
     * 批量盘点调整库存（新增方法，不修改原有adjustStock单条）
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAdjustStock(InventoryBatchAdjustDTO dto);

    void syncMaterialStockCache(BasMaterial basMaterial);
}