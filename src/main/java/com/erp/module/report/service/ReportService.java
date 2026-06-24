package com.erp.module.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.report.dto.ReportQueryDTO;
import com.erp.module.report.vo.CostTrendVO;
import com.erp.module.report.vo.DashboardVO;
import com.erp.module.report.vo.InventoryTurnoverVO;
import com.erp.module.report.vo.PurchaseSummaryVO;
import com.erp.module.report.vo.SalesSummaryVO;
import com.erp.module.system.dto.OperateLogQueryDTO;
import com.erp.module.system.entity.SysOperationLog;

public interface ReportService {

    /** 首页看板统计 */
    DashboardVO getDashboardData();

    /** 采购汇总报表 */
    PurchaseSummaryVO getPurchaseSummary(ReportQueryDTO dto);

    /** 销售汇总报表（含毛利） */
    SalesSummaryVO getSalesSummary(ReportQueryDTO dto);

    /** 库存周转率分析 */
    InventoryTurnoverVO getInventoryTurnover(ReportQueryDTO dto);

    /** 成本趋势报表 */
    CostTrendVO getCostTrend(ReportQueryDTO dto);

    /** 操作日志分页 */
    Page<SysOperationLog> pageOperateLog(OperateLogQueryDTO dto);
}