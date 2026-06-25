package com.erp.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.module.base.dto.SupplierPageDTO;
import com.erp.module.base.dto.SupplierSaveDTO;
import com.erp.module.base.entity.BasSupplier;
import com.erp.module.base.mapper.BasSupplierMapper;
import com.erp.module.base.service.BasSupplierService;
import com.erp.module.base.vo.BasSupplierVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasSupplierServiceImpl extends ServiceImpl<BasSupplierMapper, BasSupplier> implements BasSupplierService {

    @Override
    public Page<BasSupplierVO> pageList(SupplierPageDTO dto) {
        LambdaQueryWrapper<BasSupplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasSupplier::getSupplierCode, dto.getKeyword())
                    .or().like(BasSupplier::getSupplierName, dto.getKeyword()));
        }
        if (dto.getStatus() != null) wrapper.eq(BasSupplier::getStatus, dto.getStatus());
        wrapper.orderByDesc(BasSupplier::getCreateTime);
        Page<BasSupplier> entityPage = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        List<BasSupplierVO> voList = entityPage.getRecords().stream().map(entity -> {
            BasSupplierVO vo = new BasSupplierVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<BasSupplierVO> voPage = new Page<>();
        voPage.setTotal(entityPage.getTotal());
        voPage.setRecords(voList);
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        return voPage;
    }

    @Override
    public BasSupplierVO getDetail(Long id) {
        BasSupplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商数据不存在");
        }
        BasSupplierVO vo = new BasSupplierVO();
        BeanUtils.copyProperties(supplier, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSupplier(SupplierSaveDTO dto, Long loginUserId) {
        long count = count(new LambdaQueryWrapper<BasSupplier>()
                .eq(BasSupplier::getSupplierCode, dto.getSupplierCode()));
        if (count > 0) throw new BusinessException("供应商编码已存在");
        BasSupplier supplier = new BasSupplier();
        BeanUtils.copyProperties(dto, supplier);
        // supplier.setCreatorId(loginUserId);
        save(supplier);
        return supplier.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplier(SupplierSaveDTO dto, Long loginUserId) {
        Long id = dto.getId();
        BasSupplier supplier = getById(id);
        if (supplier == null) throw new BusinessException("供应商不存在");
        if (!supplier.getSupplierCode().equals(dto.getSupplierCode())) {
            long count = count(new LambdaQueryWrapper<BasSupplier>()
                    .eq(BasSupplier::getSupplierCode, dto.getSupplierCode())
                    .ne(BasSupplier::getId, id));
            if (count > 0) throw new BusinessException("供应商编码已存在");
        }
        BeanUtils.copyProperties(dto, supplier);
        // supplier.setUpdaterId(loginUserId);
        updateById(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplier(Long id) {
        removeById(id);
    }
}