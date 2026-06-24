package com.erp.module.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.entity.BasSupplier;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.base.mapper.BasSupplierMapper;
import com.erp.module.finance.dto.SuggestionQueryDTO;
import com.erp.module.finance.entity.FinPurchaseSuggestion;
import com.erp.module.finance.mapper.FinPurchaseSuggestionMapper;
import com.erp.module.finance.service.SuggestionService;
import com.erp.module.finance.vo.PurchaseSuggestionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionServiceImpl extends ServiceImpl<FinPurchaseSuggestionMapper, FinPurchaseSuggestion>
        implements SuggestionService {

    private final FinPurchaseSuggestionMapper suggestMapper;
    private final BasMaterialMapper materialMapper;
    private final BasSupplierMapper supplierMapper;

    // 手动构造器注入，完全抛弃lombok，彻底解决注解识别爆红
    public SuggestionServiceImpl(FinPurchaseSuggestionMapper suggestMapper,
                                 BasMaterialMapper materialMapper,
                                 BasSupplierMapper supplierMapper) {
        this.suggestMapper = suggestMapper;
        this.materialMapper = materialMapper;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public Page<PurchaseSuggestionVO> pageSuggest(SuggestionQueryDTO dto) {
        // 分页参数空值兜底，防止null报错
        Long pageNum = dto.getPageNum() == null ? 1L : dto.getPageNum();
        Long pageSize = dto.getPageSize() == null ? 10L : dto.getPageSize();
        Page<FinPurchaseSuggestion> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<FinPurchaseSuggestion> wrapper = new LambdaQueryWrapper<>();
        if (dto.getUrgencyLevel() != null) {
            wrapper.eq(FinPurchaseSuggestion::getUrgencyLevel, dto.getUrgencyLevel());
        }
        if (dto.getMaterialId() != null) {
            wrapper.eq(FinPurchaseSuggestion::getMaterialId, dto.getMaterialId());
        }
        Page<FinPurchaseSuggestion> data = baseMapper.selectPage(page, wrapper);

        List<PurchaseSuggestionVO> voList = data.getRecords().stream()
                .map((FinPurchaseSuggestion s) -> {
                    PurchaseSuggestionVO vo = new PurchaseSuggestionVO();
                    vo.setId(s.getId());
                    vo.setMaterialId(s.getMaterialId());
                    vo.setSupplierId(s.getSupplierId());
                    vo.setUrgencyLevel(s.getUrgencyLevel());
                    vo.setCurrentStock(s.getCurrentStock());
                    vo.setSafetyStock(s.getSafetyStock());
                    vo.setAvgWeeklyConsumption(s.getAvgWeeklyConsumption());
                    vo.setSuggestedQty(s.getSuggestedQty());
                    vo.setRefPrice(s.getRefPrice());
                    vo.setCalcTime(s.getCalcTime());

                    BasMaterial mat = materialMapper.selectById(s.getMaterialId());
                    if (mat != null) {
                        vo.setMaterialName(mat.getMaterialName());
                        vo.setMaterialCode(mat.getMaterialCode());
                    }

                    if (s.getUrgencyLevel() == 1) {
                        vo.setUrgencyText("紧急采购");
                    } else if (s.getUrgencyLevel() == 2) {
                        vo.setUrgencyText("建议补货");
                    } else {
                        vo.setUrgencyText("库存充足");
                    }

                    BasSupplier sup = supplierMapper.selectById(s.getSupplierId());
                    if (sup != null) {
                        vo.setSupplierName(sup.getSupplierName());
                    }
                    return vo;
                }).collect(Collectors.toList());

        Page<PurchaseSuggestionVO> res = new Page<>();
        res.setTotal(data.getTotal());
        res.setRecords(voList);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAll() {
        // 清空旧采购建议数据
        baseMapper.delete(new LambdaQueryWrapper<>());
        // 查询全部原料物料 type=1
        LambdaQueryWrapper<BasMaterial> matWrapper = new LambdaQueryWrapper<>();
        matWrapper.eq(BasMaterial::getMaterialType, 1);
        List<BasMaterial> rawList = materialMapper.selectList(matWrapper);

        LocalDateTime now = LocalDateTime.now();
        for (BasMaterial mat : rawList) {
            FinPurchaseSuggestion item = new FinPurchaseSuggestion();
            item.setMaterialId(mat.getId());
            item.setCurrentStock(mat.getCurrentStock());
            item.setSafetyStock(mat.getSafetyStock());

            // 近四周平均消耗固定示意值
            BigDecimal avgConsume = new BigDecimal("100");
            item.setAvgWeeklyConsumption(avgConsume);

            // 可用库存 = 当前库存 - 锁定库存
            BigDecimal availableStock = mat.getCurrentStock().subtract(mat.getLockedStock());
            BigDecimal diff = mat.getSafetyStock().subtract(availableStock);
            BigDecimal suggestQty = diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO;
            item.setSuggestedQty(suggestQty);

            // 紧急等级判断（全部替换为可用库存计算）
            if (availableStock.compareTo(mat.getSafetyStock()) <= 0) {
                item.setUrgencyLevel(1);
            } else if (availableStock.compareTo(mat.getSafetyStock().multiply(new BigDecimal("1.5"))) <= 0) {
                item.setUrgencyLevel(2);
            } else {
                item.setUrgencyLevel(3);
            }

            // 固定推荐供应商ID（业务可自行替换逻辑）
            Long recommendSupId = 1L;
            item.setSupplierId(recommendSupId);
            item.setRefPrice(mat.getLastPurchasePrice());
            item.setCalcTime(now);
            save(item);
        }
    }
}