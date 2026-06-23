package com.erp.module.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.entity.InvStockRecord;
import com.erp.module.inventory.mapper.InvPreoccupyMapper;
import com.erp.module.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final InvPreoccupyMapper invPreoccupyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void preOccupy(StockOccupyDTO dto) {
        InvPreoccupy pre = new InvPreoccupy();
        pre.setMaterialId(dto.getMaterialId());
        pre.setWarehouseId(dto.getWarehouseId());
        pre.setQty(dto.getOccupyQty());
        pre.setRefType(dto.getRefType());
        pre.setRefId(dto.getRefId());
        // 可根据业务设置超时时间
        invPreoccupyMapper.insert(pre);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseOccupy(InvPreoccupy record) {
        // 更新预占状态为已释放
        record.setPreoccupyStatus(2);
        invPreoccupyMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePreoccupyByRef(Long refId) {
        List<InvPreoccupy> list = invPreoccupyMapper.selectByRefId(refId);
        for (InvPreoccupy item : list) {
            releaseOccupy(item);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purchaseIn(Long materialId, Long warehouseId, BigDecimal inQty, BigDecimal unitPrice, String refType, Long refId) {
        // 1. 查询当前物料库存，计算加权平均成本
        LambdaQueryWrapper<InvStockRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvStockRecord::getMaterialId, materialId)
                .eq(InvStockRecord::getWarehouseId, warehouseId)
                .orderByDesc(InvStockRecord::getCreateTime);
        // 此处简化，实际项目要加悲观锁更新bas_material库存与avg_cost
        InvStockRecord record = new InvStockRecord();
        record.setMaterialId(materialId);
        record.setWarehouseId(warehouseId);
        record.setRecordType(1);
        record.setStockStatus(2);
        record.setQtyChange(inQty);
        record.setUnitCost(unitPrice);
        record.setTotalCost(inQty.multiply(unitPrice));
        record.setRefType(refType);
        record.setRefId(refId);
        // stockRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(Long materialId, Long warehouseId, BigDecimal outQty, String refType, Long refId) {
        // 出库逻辑，写入库存流水、扣减库存
        InvStockRecord record = new InvStockRecord();
        record.setMaterialId(materialId);
        record.setWarehouseId(warehouseId);
        record.setRecordType(2);
        record.setStockStatus(4);
        record.setQtyChange(outQty.multiply(new BigDecimal("-1")));
        record.setRefType(refType);
        record.setRefId(refId);
        // stockRecordMapper.insert(record);
    }
}