package com.erp.module.report.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.report.dto.ReportQueryDTO;
import com.erp.module.report.vo.DashboardVO;
import com.erp.module.report.vo.PurchaseSummaryVO;
import com.erp.module.report.vo.SalesSummaryVO;
import com.erp.module.report.vo.InventoryTurnoverVO;
import com.erp.module.report.vo.CostTrendVO;
import com.erp.module.report.service.ReportService;
import com.erp.module.system.dto.OperateLogQueryDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.system.entity.SysOperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 首页数据看板
     */
    @GetMapping("/dashboard")
    @RequirePermission("report:dashboard:view")
    public R<DashboardVO> getDashboard() {
        DashboardVO vo = reportService.getDashboardData();
        return R.ok(vo);
    }

    /**
     * 采购汇总报表（供应商/物料/月份维度）
     */
    @GetMapping("/purchase-summary")
    @RequirePermission("report:purchase:view")
    public R<PurchaseSummaryVO> purchaseSummary(ReportQueryDTO dto) {
        return R.ok(reportService.getPurchaseSummary(dto));
    }

    /**
     * 销售汇总报表（含毛利、毛利率）
     */
    @GetMapping("/sales-summary")
    @RequirePermission("report:sales:view")
    public R<SalesSummaryVO> salesSummary(ReportQueryDTO dto) {
        return R.ok(reportService.getSalesSummary(dto));
    }

    /**
     * 库存周转率分析
     */
    @GetMapping("/inventory-turnover")
    @RequirePermission("report:inventory:view")
    public R<InventoryTurnoverVO> inventoryTurnover(ReportQueryDTO dto) {
        return R.ok(reportService.getInventoryTurnover(dto));
    }

    /**
     * 成本趋势（按周/月）
     */
    @GetMapping("/cost-trend")
    @RequirePermission("report:cost:view")
    public R<CostTrendVO> costTrend(ReportQueryDTO dto) {
        return R.ok(reportService.getCostTrend(dto));
    }

    /**
     * 操作日志分页查询（仅超级管理员）
     */
    @GetMapping("/operate-log")
    @RequirePermission("report:operateLog:view")
    public R<Page<SysOperationLog>> operateLogPage(OperateLogQueryDTO dto) {
        Page<SysOperationLog> page = reportService.pageOperateLog(dto);
        return R.ok(page);
    }
}