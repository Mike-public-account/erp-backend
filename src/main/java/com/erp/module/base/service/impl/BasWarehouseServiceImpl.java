package com.erp.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.module.base.dto.WarehousePageDTO;
import com.erp.module.base.dto.WarehouseSaveDTO;
import com.erp.module.base.entity.BasWarehouse;
import com.erp.module.base.mapper.BasWarehouseMapper;
import com.erp.module.base.service.BasWarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BasWarehouseServiceImpl extends ServiceImpl<BasWarehouseMapper, BasWarehouse> implements BasWarehouseService {

    @Override
    public Page<BasWarehouse> pageList(WarehousePageDTO dto) {
        LambdaQueryWrapper<BasWarehouse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasWarehouse::getWarehouseCode, dto.getKeyword())
                    .or().like(BasWarehouse::getWarehouseName, dto.getKeyword()));
        }
        if (dto.getStatus() != null) wrapper.eq(BasWarehouse::getStatus, dto.getStatus());
        wrapper.orderByDesc(BasWarehouse::getCreateTime);
        return page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWarehouse(WarehouseSaveDTO dto) {
        long count = count(new LambdaQueryWrapper<BasWarehouse>()
                .eq(BasWarehouse::getWarehouseCode, dto.getWarehouseCode()));
        if (count > 0) throw new BusinessException("仓库编码已存在");
        BasWarehouse warehouse = new BasWarehouse();
        BeanUtils.copyProperties(dto, warehouse);
        save(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehouse(Long id, WarehouseSaveDTO dto) {
        BasWarehouse warehouse = getById(id);
        if (warehouse == null) throw new BusinessException("仓库不存在");
        if (!warehouse.getWarehouseCode().equals(dto.getWarehouseCode())) {
            long count = count(new LambdaQueryWrapper<BasWarehouse>()
                    .eq(BasWarehouse::getWarehouseCode, dto.getWarehouseCode())
                    .ne(BasWarehouse::getId, id));
            if (count > 0) throw new BusinessException("仓库编码已存在");
        }
        BeanUtils.copyProperties(dto, warehouse);
        updateById(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWarehouse(Long id) {
        removeById(id);
    }
}