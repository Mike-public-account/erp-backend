package com.erp.module.finance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.utils.BeanCopyUtil;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.finance.dto.CostQueryDTO;
import com.erp.module.finance.entity.FinCostRecord;
import com.erp.module.finance.mapper.FinCostRecordMapper;
import com.erp.module.finance.service.CostCalcService;
import com.erp.module.finance.vo.CostRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CostCalcServiceImpl extends ServiceImpl<FinCostRecordMapper, FinCostRecord> implements CostCalcService {
    private final FinCostRecordMapper costRecordMapper;
    private final BasMaterialMapper materialMapper;

    @Override
    public BigDecimal getRealtimeCost(Long productId) {
        BasMaterial mat = materialMapper.selectById(productId);
        if (mat == null) {
            return BigDecimal.ZERO;
        }
        return mat.getAvgCost() == null ? BigDecimal.ZERO : mat.getAvgCost();
    }

    @Override
    public Page<CostRecordVO> pageCost(CostQueryDTO dto) {
        // 分页参数兜底，防止null空指针
        Long pageNum = dto.getPageNum() == null ? 1L : dto.getPageNum();
        Long pageSize = dto.getPageSize() == null ? 10L : dto.getPageSize();
        Page<FinCostRecord> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<FinCostRecord> wrapper = new LambdaQueryWrapper<>();
        if (dto.getMaterialId() != null) {
            wrapper.eq(FinCostRecord::getMaterialId, dto.getMaterialId());
        }
        if (dto.getSettlePeriod() != null) {
            wrapper.eq(FinCostRecord::getSettlePeriod, dto.getSettlePeriod());
        }
        if (dto.getStartDate() != null) {
            wrapper.ge(FinCostRecord::getCalcTime, dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            wrapper.le(FinCostRecord::getCalcTime, dto.getEndDate());
        }
        wrapper.orderByDesc(FinCostRecord::getCalcTime);
        Page<FinCostRecord> data = baseMapper.selectPage(page, wrapper);

        List<CostRecordVO> voList = data.getRecords().stream()
                .map((FinCostRecord r) -> {
                    CostRecordVO vo = BeanCopyUtil.copy(r, CostRecordVO.class);
                    BasMaterial m = materialMapper.selectById(r.getMaterialId());
                    if (m != null) {
                        vo.setMaterialName(m.getMaterialName());
                    }
                    return vo;
                }).collect(Collectors.toList());

        Page<CostRecordVO> res = new Page<>();
        res.setTotal(data.getTotal());
        res.setCurrent(data.getCurrent());
        res.setSize(data.getSize());
        res.setRecords(voList);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualCalc(String period) {
        // 参数校验
        if (period == null || period.isBlank()) {
            throw new IllegalArgumentException("核算周期不能为空");
        }
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(DayOfWeek.MONDAY);
        LocalDate sunday = now.with(DayOfWeek.SUNDAY);
        weeklyCalc(monday, sunday, period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void weeklyCalc(LocalDate monday, LocalDate sunday, String period) {
        // 修复：同一周期先删除旧数据，避免重复核算重复入库
        LambdaQueryWrapper<FinCostRecord> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(FinCostRecord::getSettlePeriod, period);
        remove(delWrapper);

        List<FinCostRecord> workSumList = costRecordMapper.sumWorkOrderByRange(monday, sunday);
        final int scale6 = 6;
        final int scale4 = 4;
        final BigDecimal hundred = new BigDecimal("100");

        for (FinCostRecord s : workSumList) {
            BigDecimal totalInput = s.getTotalInputQty() == null ? BigDecimal.ZERO : s.getTotalInputQty();
            BigDecimal totalCost = s.getTotalMaterialCost() == null ? BigDecimal.ZERO : s.getTotalMaterialCost();
            if (totalInput.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal unitCost = totalCost.divide(totalInput, scale6, RoundingMode.HALF_UP);

            BigDecimal saleQty = costRecordMapper.sumSaleQty(s.getMaterialId(), monday, sunday);
            saleQty = saleQty == null ? BigDecimal.ZERO : saleQty;
            BigDecimal saleAmount = costRecordMapper.sumSaleAmount(s.getMaterialId(), monday, sunday);
            saleAmount = saleAmount == null ? BigDecimal.ZERO : saleAmount;

            BigDecimal saleCost = saleQty.multiply(unitCost);
            BigDecimal grossProfit = saleAmount.subtract(saleCost);
            BigDecimal grossMargin = BigDecimal.ZERO;

            if (saleAmount.compareTo(BigDecimal.ZERO) > 0) {
                grossMargin = grossProfit.divide(saleAmount, scale4, RoundingMode.HALF_UP).multiply(hundred);
            }

            s.setSettleType(2);
            s.setSettlePeriod(period);
            s.setUnitCost(unitCost);
            s.setSaleQty(saleQty);
            s.setSaleAmount(saleAmount);
            s.setGrossProfit(grossProfit);
            s.setGrossMargin(grossMargin);
            save(s);
        }
    }
}