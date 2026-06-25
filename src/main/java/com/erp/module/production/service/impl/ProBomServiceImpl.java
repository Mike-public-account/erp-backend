package com.erp.module.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.production.dto.BomPageDTO;
import com.erp.module.production.dto.BomSaveDTO;
import com.erp.module.production.entity.ProBom;
import com.erp.module.production.mapper.ProBomMapper;
import com.erp.module.production.service.ProBomService;
import com.erp.module.production.vo.BomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProBomServiceImpl extends ServiceImpl<ProBomMapper, ProBom>
        implements ProBomService {

    private final ProBomMapper bomMapper;
    private final BasMaterialMapper materialMapper;

    @Override
    public Page<BomVO> bomPage(BomPageDTO dto) {
        Page<ProBom> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ProBom> wrapper = new LambdaQueryWrapper<>();
        if (dto.getProductId() != null) {
            wrapper.eq(ProBom::getProductId, dto.getProductId());
        }
        wrapper.eq(ProBom::getIsDeleted, 0);
        IPage<ProBom> dataPage = bomMapper.selectPage(page, wrapper);

        Page<BomVO> voPage = new Page<>();
        voPage.setTotal(dataPage.getTotal());
        voPage.setCurrent(dataPage.getCurrent());
        voPage.setSize(dataPage.getSize());

        List<ProBom> recordList = dataPage.getRecords();
        List<BomVO> voList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(recordList)) {
            voList = recordList.stream().map(this::convertToVO).collect(Collectors.toList());
        }
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBom(BomSaveDTO dto) {
        Long productId = dto.getProductMaterialId();
        Long rawMaterialId = dto.getRawMaterialId();
        BigDecimal perUnitQty = dto.getPerUnitQty();
        BigDecimal lossRate = dto.getLossRate() == null ? BigDecimal.ZERO : dto.getLossRate();
        String remark = dto.getRemark();

        BasMaterial productMat = materialMapper.selectById(productId);
        BasMaterial rawMat = materialMapper.selectById(rawMaterialId);
        if (productMat == null) {
            throw new BusinessException("成品物料不存在");
        }
        if (rawMat == null) {
            throw new BusinessException("原料物料不存在");
        }
        if (!Integer.valueOf(3).equals(productMat.getMaterialType())) {
            throw new BusinessException("仅成品物料可创建BOM");
        }
        if (!Integer.valueOf(1).equals(rawMat.getMaterialType())) {
            throw new BusinessException("BOM仅支持绑定原料物料");
        }

        LambdaQueryWrapper<ProBom> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(ProBom::getProductId, productId)
                .eq(ProBom::getMaterialId, rawMaterialId)
                .eq(ProBom::getIsDeleted, 0);
        ProBom existBom = bomMapper.selectOne(existWrapper);
        if (existBom != null) {
            throw new BusinessException("该成品已存在此原料BOM记录，请勿重复添加");
        }

        ProBom bom = new ProBom();
        bom.setProductId(productId);
        bom.setMaterialId(rawMaterialId);
        bom.setQtyPerUnit(perUnitQty);
        bom.setLossRate(lossRate);
        bom.setUnit(rawMat.getUnit());
        bom.setRemark(remark);
        bomMapper.insert(bom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBom(Long id, BomSaveDTO dto) {
        ProBom bom = bomMapper.selectById(id);
        if (bom == null || bom.getIsDeleted() == 1) {
            throw new BusinessException("BOM记录不存在");
        }

        Long rawMaterialId = dto.getMaterialId();
        BigDecimal perUnitQty = dto.getQtyPerUnit();
        BigDecimal lossRate = dto.getLossRate() == null ? BigDecimal.ZERO : dto.getLossRate();
        String remark = dto.getRemark();

        BasMaterial rawMat = materialMapper.selectById(rawMaterialId);
        if (rawMat == null) {
            throw new BusinessException("原料物料不存在");
        }

        bom.setMaterialId(rawMaterialId);
        bom.setQtyPerUnit(perUnitQty);
        bom.setLossRate(lossRate);
        bom.setUnit(rawMat.getUnit());
        bom.setRemark(remark);
        bomMapper.updateById(bom);
    }

    @Override
    public BomVO getBomInfo(Long id) {
        ProBom bom = bomMapper.selectById(id);
        if (bom == null || bom.getIsDeleted() == 1) {
            throw new BusinessException("BOM记录不存在");
        }
        return convertToVO(bom);
    }

    @Override
    public List<BomVO> getBomByProductId(Long productId) {
        LambdaQueryWrapper<ProBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProBom::getProductId, productId)
                .eq(ProBom::getIsDeleted, 0);
        List<ProBom> bomList = bomMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(bomList)) {
            return new ArrayList<>();
        }
        return bomList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBom(Long id) {
        ProBom bom = getById(id);
        if (bom == null) {
            throw new BusinessException("该BOM记录不存在");
        }
        removeById(id);
    }

    @Override
    public BigDecimal calcProductBomCost(Long productId) {
        // 后续自行实现成本递归计算逻辑，此处占位
        return BigDecimal.ZERO;
    }

    private BomVO convertToVO(ProBom bom) {
        BomVO vo = new BomVO();
        vo.setId(bom.getId());
        vo.setProductId(bom.getProductId());
        vo.setMaterialId(bom.getMaterialId());
        vo.setProductMaterialId(bom.getProductId());
        vo.setRawMaterialId(bom.getMaterialId());
        vo.setPerUnitQty(bom.getQtyPerUnit());
        vo.setLossRate(bom.getLossRate());
        vo.setRemark(bom.getRemark());
        vo.setCreateTime(bom.getCreateTime());
        vo.setUpdateTime(bom.getUpdateTime());

        BasMaterial rawMat = materialMapper.selectById(bom.getMaterialId());
        if (rawMat != null) {
            vo.setRawMaterialName(rawMat.getMaterialName());
            vo.setRawMaterialCode(rawMat.getMaterialCode());
        }
        BasMaterial productMat = materialMapper.selectById(bom.getProductId());
        if (productMat != null) {
            vo.setProductMaterialName(productMat.getMaterialName());
            vo.setProductMaterialCode(productMat.getMaterialCode());
        }
        return vo;
    }
}