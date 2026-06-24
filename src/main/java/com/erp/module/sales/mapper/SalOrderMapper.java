package com.erp.module.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.report.vo.SalesSummaryVO;
import com.erp.module.sales.entity.SalOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SalOrderMapper extends BaseMapper<SalOrder> {

    /**
     * 统计今日销售单数量
     */
    @Select("SELECT COUNT(*) FROM sal_order WHERE DATE(create_time) = #{today} AND is_deleted = 0")
    Long countTodayOrder(@Param("today") LocalDate today);

    /**
     * 时间段销售汇总，含成本、毛利、毛利率（按客户分组）
     */
    @Select("""
        SELECT c.customer_name AS groupName,
               SUM(od.sale_qty) AS saleQty,
               SUM(od.sale_amount) AS saleAmount,
               SUM(od.cost_amount) AS saleCost,
               SUM(od.sale_amount - od.cost_amount) AS grossProfit,
               CASE WHEN SUM(od.sale_amount) > 0 
                    THEN ROUND(SUM(od.sale_amount - od.cost_amount) / SUM(od.sale_amount) * 100, 2) 
                    ELSE 0 END AS grossMargin
        FROM sal_order o
        LEFT JOIN sal_order_detail od ON o.id = od.order_id
        LEFT JOIN bas_customer c ON o.customer_id = c.id
        WHERE DATE(o.create_time) BETWEEN #{start} AND #{end}
          AND o.is_deleted = 0
        GROUP BY o.customer_id, c.customer_name
    """)
    List<SalesSummaryVO.Item> sumSalesWithProfit(
            @Param("start") LocalDate startDate,
            @Param("end") LocalDate endDate
    );
}