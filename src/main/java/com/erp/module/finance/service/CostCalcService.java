package com.erp.module.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.finance.dto.CostQueryDTO;
import com.erp.module.finance.entity.FinCostRecord;
import com.erp.module.finance.vo.CostRecordVO;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface CostCalcService extends IService<FinCostRecord> {
    // 实时查询成品当前加权成本
    BigDecimal getRealtimeCost(Long productId);
    // 按周期分页成本记录
    Page<CostRecordVO> pageCost(CostQueryDTO dto);
    // 手动触发周期核算
    void manualCalc(String period);
    // 周结算核心核算
    void weeklyCalc(LocalDate monday, LocalDate sunday, String period);
}