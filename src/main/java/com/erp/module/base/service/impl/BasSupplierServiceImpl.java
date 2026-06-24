package com.erp.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.module.base.dto.SupplierPageDTO;
import com.erp.module.base.dto.SupplierSaveDTO;
import com.erp.module.base.entity.BasSupplier;
import com.erp.module.base.mapper.BasSupplierMapper;
import com.erp.module.base.service.BasSupplierService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BasSupplierServiceImpl extends ServiceImpl<BasSupplierMapper, BasSupplier> implements BasSupplierService {

    @Override
    public Page<BasSupplier> pageList(SupplierPageDTO dto) {
        LambdaQueryWrapper<BasSupplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasSupplier::getSupplierCode, dto.getKeyword())
                    .or().like(BasSupplier::getSupplierName, dto.getKeyword()));
        }
        if (dto.getStatus() != null) wrapper.eq(BasSupplier::getStatus, dto.getStatus());
        wrapper.orderByDesc(BasSupplier::getCreateTime);
        return page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSupplier(SupplierSaveDTO dto) {
        long count = count(new LambdaQueryWrapper<BasSupplier>()
                .eq(BasSupplier::getSupplierCode, dto.getSupplierCode()));
        if (count > 0) throw new BusinessException("供应商编码已存在");
        BasSupplier supplier = new BasSupplier();
        BeanUtils.copyProperties(dto, supplier);
        save(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplier(Long id, SupplierSaveDTO dto) {
        BasSupplier supplier = getById(id);
        if (supplier == null) throw new BusinessException("供应商不存在");
        if (!supplier.getSupplierCode().equals(dto.getSupplierCode())) {
            long count = count(new LambdaQueryWrapper<BasSupplier>()
                    .eq(BasSupplier::getSupplierCode, dto.getSupplierCode())
                    .ne(BasSupplier::getId, id));
            if (count > 0) throw new BusinessException("供应商编码已存在");
        }
        BeanUtils.copyProperties(dto, supplier);
        updateById(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplier(Long id) {
        removeById(id);
    }
}