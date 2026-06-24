package com.erp.module.inventory.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.constant.CacheKey;
import com.erp.common.exception.BusinessException;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.inventory.dto.InventoryBatchAdjustDTO;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.entity.InvStockRecord;
import com.erp.module.inventory.mapper.InvPreoccupyMapper;
import com.erp.module.inventory.mapper.InvStockRecordMapper;
import com.erp.module.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.inventory.dto.StockAdjustDTO;
import com.erp.module.inventory.dto.StockQueryDTO;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final InvPreoccupyMapper invPreoccupyMapper;
    private final InvStockRecordMapper stockRecordMapper;
    private final BasMaterialMapper materialMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void preOccupy(StockOccupyDTO dto) {
        InvPreoccupy pre = new InvPreoccupy();
        pre.setMaterialId(dto.getMaterialId());
        pre.setWarehouseId(dto.getWarehouseId());
        pre.setQty(dto.getQty());
        pre.setRefType(dto.getRefType());
        pre.setRefId(dto.getRefId());
        pre.setPreoccupyStatus(1);
        invPreoccupyMapper.insert(pre);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseOccupy(InvPreoccupy record) {
        record.setPreoccupyStatus(2);
        invPreoccupyMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePreoccupyByRef(Long refId, String refType) {
        LambdaQueryWrapper<InvPreoccupy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvPreoccupy::getRefId, refId);
        wrapper.eq(InvPreoccupy::getRefType, refType);
        wrapper.eq(InvPreoccupy::getPreoccupyStatus, 1);
        List<InvPreoccupy> list = invPreoccupyMapper.selectList(wrapper);
        for (InvPreoccupy item : list) {
            releaseOccupy(item);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purchaseIn(Long materialId, Long warehouseId, BigDecimal inQty,
                           BigDecimal unitPrice, String refType, Long refId,
                           Long refItemId, Long operatorId) {
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
        record.setRefItemId(refItemId);
        record.setOperatorId(operatorId);
        stockRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(Long materialId, Long warehouseId, BigDecimal outQty,
                         String refType, Long refId, Long operatorId) {
        InvStockRecord record = new InvStockRecord();
        record.setMaterialId(materialId);
        record.setWarehouseId(warehouseId);
        record.setRecordType(2);
        record.setStockStatus(4);
        record.setQtyChange(outQty.multiply(new BigDecimal("-1")));
        record.setRefType(refType);
        record.setRefId(refId);
        record.setOperatorId(operatorId);
        stockRecordMapper.insert(record);
    }
    @Override
    public IPage<BasMaterial> pageStock(Page<BasMaterial> page, StockQueryDTO dto) {
        LambdaQueryWrapper<BasMaterial> wrapper = new LambdaQueryWrapper<>();
        // 物料名称/编码模糊查询
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(BasMaterial::getMaterialCode, dto.getKeyword())
                    .or().like(BasMaterial::getMaterialName, dto.getKeyword()));
        }
        // 物料类型
        if (dto.getMaterialType() != null) {
            wrapper.eq(BasMaterial::getMaterialType, dto.getMaterialType());
        }
        // 分类
        if (dto.getCategoryId() != null) {
            wrapper.eq(BasMaterial::getCategoryId, dto.getCategoryId());
        }
        // 仅预警
        if (Integer.valueOf(1).equals(dto.getWarnFlag())) {
            wrapper.apply("current_stock - locked_stock <= safety_stock");
        }
        wrapper.orderByDesc(BasMaterial::getId);
        return materialMapper.selectPage(page, wrapper);
    }

    @Override
    public List<InvStockRecord> getRecordByMaterialId(Long materialId) {
        LambdaQueryWrapper<InvStockRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvStockRecord::getMaterialId, materialId);
        wrapper.orderByDesc(InvStockRecord::getCreateTime);
        return stockRecordMapper.selectList(wrapper);
    }

    @Override
    public List<BasMaterial> getWarningMaterial() {
        LambdaQueryWrapper<BasMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("current_stock - locked_stock <= safety_stock");
        return materialMapper.selectList(wrapper);
    }

    @Override
    public List<InvPreoccupy> getAllPreoccupy() {
        LambdaQueryWrapper<InvPreoccupy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvPreoccupy::getPreoccupyStatus, 1);
        wrapper.orderByDesc(InvPreoccupy::getCreateTime);
        return invPreoccupyMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustStock(StockAdjustDTO dto) {
        // 1、查询原有物料库存
        BasMaterial mat = materialMapper.selectById(dto.getMaterialId());
        if (mat == null) {
            throw new RuntimeException("物料不存在");
        }
        BigDecimal oldStock = mat.getCurrentStock();
        BigDecimal diff = dto.getNewStock().subtract(oldStock);

        // 2、更新物料实际库存
        mat.setCurrentStock(dto.getNewStock());
        materialMapper.updateById(mat);

        // 3、生成盘点库存流水
        InvStockRecord record = new InvStockRecord();
        record.setMaterialId(dto.getMaterialId());
        record.setWarehouseId(dto.getWarehouseId());
        record.setRecordType(3); // 盘点调整
        record.setStockStatus(diff.compareTo(BigDecimal.ZERO) >= 0 ? 2 : 4);
        record.setQtyChange(diff);
        record.setQtyAfter(dto.getNewStock());
        record.setRefType("STOCK_ADJUST");
        record.setRemark(dto.getAdjustRemark());
        stockRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdjustStock(InventoryBatchAdjustDTO dto) {
        List<InventoryBatchAdjustDTO.AdjustItemDTO> itemList = dto.getItemList();
        for (InventoryBatchAdjustDTO.AdjustItemDTO item : itemList) {
            // 悲观锁锁定物料
            BasMaterial mat = materialMapper.selectById(item.getMaterialId());
            if (mat == null) {
                throw new BusinessException("物料ID[" + item.getMaterialId() + "]不存在");
            }
            // 计算新库存，拦截负库存
            BigDecimal newStock = mat.getCurrentStock().add(item.getAdjustQty());
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("物料[" + mat.getMaterialName() + "]调整后库存不可为负数");
            }
            // 更新物料库存
            mat.setCurrentStock(newStock);
            materialMapper.updateById(mat);

            // 写入盘点调整流水 record_type=8
            InvStockRecord record = new InvStockRecord();
            record.setMaterialId(item.getMaterialId());
            record.setWarehouseId(item.getWarehouseId());
            record.setRecordType(8);
            record.setStockStatus(newStock.compareTo(BigDecimal.ZERO) >= 0 ? 2 : 4);
            record.setQtyChange(item.getAdjustQty());
            record.setQtyAfter(newStock);
            record.setRemark(dto.getAdjustRemark() + " | " + item.getRemark());
            stockRecordMapper.insert(record);
        }
    }

    @Override
    public void syncMaterialStockCache(BasMaterial basMaterial) {
        String cacheKey = CacheKey.MATERIAL_STOCK + basMaterial.getId();
        String json = JSON.toJSONString(basMaterial);
        // 缓存30分钟过期
        redisTemplate.opsForValue().set(cacheKey, json, 30, TimeUnit.MINUTES);
    }
}