package com.erp.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.finance.mapper.FinCostRecordMapper;
import com.erp.module.purchase.mapper.PurOrderMapper;
import com.erp.module.report.dto.ReportQueryDTO;
import com.erp.module.report.service.ReportService;
import com.erp.module.report.vo.CostTrendVO;
import com.erp.module.report.vo.DashboardVO;
import com.erp.module.report.vo.InventoryTurnoverVO;
import com.erp.module.report.vo.PurchaseSummaryVO;
import com.erp.module.report.vo.SalesSummaryVO;
import com.erp.module.sales.mapper.SalOrderMapper;
import com.erp.module.system.dto.OperateLogQueryDTO;
import com.erp.module.system.entity.SysOperationLog;
import com.erp.module.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PurOrderMapper purOrderMapper;
    private final SalOrderMapper salOrderMapper;
    private final BasMaterialMapper materialMapper;
    private final FinCostRecordMapper costRecordMapper;
    private final SysOperationLogMapper operateLogMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();
        LocalDate today = LocalDate.now();
        // 今日采购单数
        vo.setTodayPurchaseCount(purOrderMapper.countTodayOrder(today));
        // 今日销售单数
        vo.setTodaySalesCount(salOrderMapper.countTodayOrder(today));
        // 在途采购单数
        vo.setTransitPurchase(purOrderMapper.countTransitOrder());
        // 库存预警物料数量
        vo.setWarningMaterialCount(materialMapper.countSafetyWarning());
        return vo;
    }

    @Override
    public PurchaseSummaryVO getPurchaseSummary(ReportQueryDTO dto) {
        PurchaseSummaryVO vo = new PurchaseSummaryVO();
        // 按时间/供应商/物料分组统计采购金额、数量
        List<PurchaseSummaryVO.Item> itemList = purOrderMapper.sumPurchase(dto.getStartDate(), dto.getEndDate());
        vo.setList(itemList);
        vo.setTotalAmount(itemList.stream()
                .map(PurchaseSummaryVO.Item::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }

    @Override
    public SalesSummaryVO getSalesSummary(ReportQueryDTO dto) {
        SalesSummaryVO vo = new SalesSummaryVO();
        List<SalesSummaryVO.Item> itemList = salOrderMapper.sumSalesWithProfit(dto.getStartDate(), dto.getEndDate());
        vo.setList(itemList);
        BigDecimal totalSale = itemList.stream().map(SalesSummaryVO.Item::getSaleAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = itemList.stream().map(SalesSummaryVO.Item::getGrossProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSaleAmount(totalSale);
        vo.setTotalGrossProfit(totalProfit);
        if (totalSale.compareTo(BigDecimal.ZERO) > 0) {
            vo.setTotalGrossMargin(totalProfit.divide(totalSale, 4).multiply(new BigDecimal("100")));
        }
        return vo;
    }

    @Override
    public InventoryTurnoverVO getInventoryTurnover(ReportQueryDTO dto) {
        InventoryTurnoverVO vo = new InventoryTurnoverVO();
        List<InventoryTurnoverVO.Item> data = materialMapper.calcTurnoverRate(dto.getStartDate(), dto.getEndDate());
        vo.setItemList(data);
        return vo;
    }

    @Override
    public CostTrendVO getCostTrend(ReportQueryDTO dto) {
        CostTrendVO vo = new CostTrendVO();
        List<CostTrendVO.Item> trendList = costRecordMapper.selectCostTrend(dto.getStartDate(), dto.getEndDate());
        vo.setTrendList(trendList);
        return vo;
    }

    @Override
    public Page<SysOperationLog> pageOperateLog(OperateLogQueryDTO dto) {
        Page<SysOperationLog> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (dto.getModule() != null) {
            wrapper.like(SysOperationLog::getModule, dto.getModule());
        }
        if (dto.getUsername() != null) {
            wrapper.like(SysOperationLog::getUsername, dto.getUsername());
        }
        if (dto.getStart() != null && dto.getEnd() != null) {
            wrapper.between(SysOperationLog::getCreateTime, dto.getStart(), dto.getEnd());
        }
        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        return operateLogMapper.selectPage(page, wrapper);
    }
}