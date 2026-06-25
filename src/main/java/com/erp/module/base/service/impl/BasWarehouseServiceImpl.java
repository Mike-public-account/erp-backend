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
import com.erp.module.base.vo.BasWarehouseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasWarehouseServiceImpl extends ServiceImpl<BasWarehouseMapper, BasWarehouse> implements BasWarehouseService {

    @Override
    public Page<BasWarehouseVO> pageList(WarehousePageDTO dto) {
        LambdaQueryWrapper<BasWarehouse> wrapper = new LambdaQueryWrapper<>();
        // 编码/名称模糊搜索
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasWarehouse::getWarehouseCode, dto.getKeyword())
                    .or().like(BasWarehouse::getWarehouseName, dto.getKeyword()));
        }
        // 状态筛选
        if (dto.getStatus() != null) {
            wrapper.eq(BasWarehouse::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(BasWarehouse::getCreateTime);
        // 查实体分页
        Page<BasWarehouse> entityPage = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        // 实体批量拷贝转为VO
        List<BasWarehouseVO> voList = entityPage.getRecords().stream().map(entity -> {
            BasWarehouseVO vo = new BasWarehouseVO();
            BeanUtils.copyProperties(entity, vo);
            // 如需负责人名称等关联字段，可在此补充mapper联查赋值
            return vo;
        }).collect(Collectors.toList());

        // 组装VO分页对象返回
        Page<BasWarehouseVO> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public BasWarehouseVO getDetail(Long id) {
        BasWarehouse warehouse = getById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库数据不存在");
        }
        BasWarehouseVO vo = new BasWarehouseVO();
        BeanUtils.copyProperties(warehouse, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addWarehouse(WarehouseSaveDTO dto, Long loginUserId) {
        // 仓库编码唯一校验
        long count = count(new LambdaQueryWrapper<BasWarehouse>()
                .eq(BasWarehouse::getWarehouseCode, dto.getWarehouseCode()));
        if (count > 0) {
            throw new BusinessException("仓库编码已存在");
        }
        BasWarehouse warehouse = new BasWarehouse();
        BeanUtils.copyProperties(dto, warehouse);
        save(warehouse);
        return warehouse.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehouse(WarehouseSaveDTO dto, Long loginUserId) {
        Long id = dto.getId();
        BasWarehouse warehouse = getById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        // 修改编码时校验重复
        if (!warehouse.getWarehouseCode().equals(dto.getWarehouseCode())) {
            long count = count(new LambdaQueryWrapper<BasWarehouse>()
                    .eq(BasWarehouse::getWarehouseCode, dto.getWarehouseCode())
                    .ne(BasWarehouse::getId, id));
            if (count > 0) {
                throw new BusinessException("仓库编码已存在");
            }
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