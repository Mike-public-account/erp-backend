package com.erp.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.ExcelUtil;
import com.erp.module.base.dto.MaterialBatchDTO;
import com.erp.module.base.dto.MaterialPageDTO;
import com.erp.module.base.dto.MaterialSaveDTO;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.base.service.BasMaterialService;
import com.erp.module.base.vo.BasMaterialVO;
import com.erp.module.base.vo.MaterialStockSummaryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BasMaterialServiceImpl extends ServiceImpl<BasMaterialMapper, BasMaterial> implements BasMaterialService{

    private final BasMaterialMapper materialMapper;

    public BasMaterialServiceImpl(BasMaterialMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    // ========== 原有方法 ==========
    @Override
    public Page<BasMaterial> pageList(MaterialPageDTO dto) {
        LambdaQueryWrapper<BasMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BasMaterial::getIsDeleted, GlobalConstant.NOT_DELETE);
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasMaterial::getMaterialCode, dto.getKeyword())
                    .or().like(BasMaterial::getMaterialName, dto.getKeyword()));
        }
        if (dto.getMaterialType() != null) {
            wrapper.eq(BasMaterial::getMaterialType, dto.getMaterialType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(BasMaterial::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(BasMaterial::getCreateTime);
        return page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaterial(MaterialSaveDTO dto) {
        long count = count(new LambdaQueryWrapper<BasMaterial>()
                .eq(BasMaterial::getMaterialCode, dto.getMaterialCode())
                .eq(BasMaterial::getIsDeleted, GlobalConstant.NOT_DELETE));
        if (count > 0) throw new BusinessException("物料编码已存在");

        BasMaterial material = new BasMaterial();
        BeanUtils.copyProperties(dto, material);
        save(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterial(Long id, MaterialSaveDTO dto) {
        BasMaterial material = getById(id);
        if (material == null) throw new BusinessException("物料不存在");

        if (!material.getMaterialCode().equals(dto.getMaterialCode())) {
            long count = count(new LambdaQueryWrapper<BasMaterial>()
                    .eq(BasMaterial::getMaterialCode, dto.getMaterialCode())
                    .eq(BasMaterial::getIsDeleted, GlobalConstant.NOT_DELETE)
                    .ne(BasMaterial::getId, id));
            if (count > 0) throw new BusinessException("物料编码已存在");
        }

        BeanUtils.copyProperties(dto, material);
        updateById(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterial(Long id) {
        removeById(id);
    }

    @Override
    public BasMaterialVO getStockSummary(Long id) {
        BasMaterial material = getById(id);
        if (material == null) throw new BusinessException("物料不存在");

        BasMaterialVO vo = new BasMaterialVO();
        BeanUtils.copyProperties(material, vo);
        vo.setAvailableStock(material.getCurrentStock().subtract(material.getLockedStock()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(MultipartFile file) throws Exception {
        List<BasMaterial> list = ExcelUtil.importExcel(file, BasMaterial.class);
        saveBatch(list);
    }

    // 修复：增加dto参数
    @Override
    public void exportExcel(MaterialPageDTO dto, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<BasMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BasMaterial::getIsDeleted, GlobalConstant.NOT_DELETE);
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(BasMaterial::getMaterialCode, dto.getKeyword())
                    .or().like(BasMaterial::getMaterialName, dto.getKeyword());
        }
        if (dto.getMaterialType() != null) {
            wrapper.eq(BasMaterial::getMaterialType, dto.getMaterialType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(BasMaterial::getStatus, dto.getStatus());
        }
        List<BasMaterial> list = list(wrapper);
        ExcelUtil.export(list, BasMaterial.class, "物料列表.xlsx", response);
    }

    // ========== 新增批量 ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveOrUpdate(MaterialBatchDTO batchDTO) {
        List<MaterialSaveDTO> dtoList = batchDTO.getList();
        if (CollectionUtils.isEmpty(dtoList)) return;

        // 新增物料列表
        List<BasMaterial> addList = dtoList.stream()
                .filter(dto -> dto.getId() == null)
                .map(dto -> {
                    BasMaterial mat = new BasMaterial();
                    BeanUtils.copyProperties(dto, mat);
                    return mat;
                }).collect(Collectors.toList());

        // 更新物料列表
        List<BasMaterial> updateList = dtoList.stream()
                .filter(dto -> dto.getId() != null)
                .map(dto -> {
                    BasMaterial mat = new BasMaterial();
                    BeanUtils.copyProperties(dto, mat);
                    return mat;
                }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(addList)) saveBatch(addList);
        if (!CollectionUtils.isEmpty(updateList)) updateBatchById(updateList);
    }

    // ========== 库存汇总 ==========
    @Override
    public List<MaterialStockSummaryVO> getAllStockSummary(MaterialPageDTO dto) {
        return materialMapper.selectStockSummary(dto);
    }
}