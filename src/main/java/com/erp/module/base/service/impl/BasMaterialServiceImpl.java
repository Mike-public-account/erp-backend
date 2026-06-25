package com.erp.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasMaterialServiceImpl extends ServiceImpl<BasMaterialMapper, BasMaterial> implements BasMaterialService {

    @Override
    public Page<BasMaterialVO> pageList(MaterialPageDTO dto) {
        LambdaQueryWrapper<BasMaterial> wrapper = new LambdaQueryWrapper<>();
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
        Page<BasMaterial> entityPage = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        List<BasMaterialVO> voList = entityPage.getRecords().stream().map(entity -> {
            BasMaterialVO vo = new BasMaterialVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<BasMaterialVO> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public BasMaterialVO getDetail(Long id) {
        BasMaterial material = getById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        BasMaterialVO vo = new BasMaterialVO();
        BeanUtils.copyProperties(material, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addMaterial(MaterialSaveDTO dto) {
        long existCount = count(new LambdaQueryWrapper<BasMaterial>()
                .eq(BasMaterial::getMaterialCode, dto.getMaterialCode()));
        if (existCount > 0) {
            throw new BusinessException("物料编码已存在");
        }
        BasMaterial material = new BasMaterial();
        BeanUtils.copyProperties(dto, material);
        save(material);
        return material.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterial(Long id, MaterialSaveDTO dto) {
        BasMaterial material = getById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        if (!material.getMaterialCode().equals(dto.getMaterialCode())) {
            long existCount = count(new LambdaQueryWrapper<BasMaterial>()
                    .eq(BasMaterial::getMaterialCode, dto.getMaterialCode())
                    .ne(BasMaterial::getId, id));
            if (existCount > 0) {
                throw new BusinessException("物料编码已存在");
            }
        }
        BeanUtils.copyProperties(dto, material);
        material.setId(id);
        updateById(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterial(Long id) {
        removeById(id);
    }

    @Override
    public MaterialStockSummaryVO getStockSummary(Long id) {
        // 此处为模板，实际需联查库存表组装库存数据
        BasMaterial material = getById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        MaterialStockSummaryVO vo = new MaterialStockSummaryVO();
        BeanUtils.copyProperties(material, vo);
        // TODO 填充库存数量、可用库存等
        return vo;
    }

    @Override
    public List<MaterialStockSummaryVO> getAllStockSummary(MaterialPageDTO dto) {
        LambdaQueryWrapper<BasMaterial> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasMaterial::getMaterialCode, dto.getKeyword())
                    .or().like(BasMaterial::getMaterialName, dto.getKeyword()));
        }
        List<BasMaterial> list = list(wrapper);
        return list.stream().map(item -> {
            MaterialStockSummaryVO vo = new MaterialStockSummaryVO();
            BeanUtils.copyProperties(item, vo);
            // TODO 批量填充库存
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveOrUpdate(MaterialBatchDTO batchDTO) {
        // 把 batch 改为 batchDTO
        List<MaterialSaveDTO> itemList = batchDTO.getList();
        for (MaterialSaveDTO dto : itemList) {
            if (dto.getId() == null) {
                addMaterial(dto);
            } else {
                updateMaterial(dto.getId(), dto);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(MultipartFile file) throws Exception {
        List<MaterialSaveDTO> dtoList = ExcelUtil.importExcel(file, MaterialSaveDTO.class);
        for (MaterialSaveDTO dto : dtoList) {
            addMaterial(dto);
        }
    }

    @Override
    public void exportExcel(MaterialPageDTO dto, HttpServletResponse response) throws IOException {
        Page<BasMaterialVO> voPage = pageList(dto);
        ExcelUtil.export(voPage.getRecords(), BasMaterialVO.class, "物料清单", response);
    }
}