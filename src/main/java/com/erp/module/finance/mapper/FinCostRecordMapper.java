package com.erp.module.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.finance.entity.FinCostRecord;
import com.erp.module.report.vo.CostTrendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FinCostRecordMapper extends BaseMapper<FinCostRecord> {
    // 按周期+成品汇总完工投产、原料总成本
    List<FinCostRecord> sumWorkOrderByRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // 周期销售总额（3参数不变）
    BigDecimal sumSaleAmount(@Param("materialId") Long materialId,
                             @Param("start") LocalDate start,
                             @Param("end") LocalDate end);

    // 【修复】补充 end 参数，和 sumSaleAmount 统一三参数
    BigDecimal sumSaleQty(@Param("materialId") Long materialId,
                          @Param("start") LocalDate start,
                          @Param("end") LocalDate end);

    /**
     * 按结算周期分组，查询时间段内成品成本趋势
     */
    @Select("""
        SELECT
            settle_period AS period,
            SUM(total_material_cost) AS totalCost,
            ROUND(AVG(unit_cost), 6) AS unitCost
        FROM fin_cost_record
        WHERE DATE(calc_time) BETWEEN #{start} AND #{end}
        GROUP BY settle_period
        ORDER BY settle_period ASC
    """)
    List<CostTrendVO.Item> selectCostTrend(
            @Param("start") LocalDate startDate,
            @Param("end") LocalDate endDate
    );
}