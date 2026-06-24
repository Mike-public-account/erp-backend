package com.erp.module.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.purchase.entity.PurOrder;
import com.erp.module.report.vo.PurchaseSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PurOrderMapper extends BaseMapper<PurOrder> {

    /**
     * 统计今日采购单数量
     */
    @Select("SELECT COUNT(*) FROM pur_order WHERE DATE(create_time) = #{today} AND is_deleted = 0")
    Long countTodayOrder(@Param("today") LocalDate today);

    /**
     * 统计在途采购单（待入库状态）
     */
    @Select("SELECT COUNT(*) FROM pur_order WHERE order_status = 1 AND is_deleted = 0")
    Long countTransitOrder();

    /**
     * 时间段采购汇总（按供应商分组）
     */
    @Select("""
        SELECT s.supplier_name AS groupName,
               SUM(od.order_qty) AS qty,
               SUM(od.order_amount) AS amount
        FROM pur_order o
        LEFT JOIN pur_order_detail od ON o.id = od.order_id
        LEFT JOIN bas_supplier s ON o.supplier_id = s.id
        WHERE DATE(o.create_time) BETWEEN #{start} AND #{end}
          AND o.is_deleted = 0
        GROUP BY o.supplier_id, s.supplier_name
    """)
    List<PurchaseSummaryVO.Item> sumPurchase(
            @Param("start") LocalDate startDate,
            @Param("end") LocalDate endDate
    );
    // 仅新增定时任务需要方法
    @Update("UPDATE pur_order SET order_status = #{status}, update_time = NOW() WHERE id = #{id} AND is_deleted = 0")
    int updateOrderStatusById(@Param("id") Long id, @Param("status") Integer status);
}