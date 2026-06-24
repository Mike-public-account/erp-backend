package com.erp.module.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.base.dto.MaterialPageDTO;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.vo.MaterialStockSummaryVO;
import com.erp.module.report.vo.InventoryTurnoverVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 物料Mapper
 */
public interface BasMaterialMapper extends BaseMapper<BasMaterial> {

    /**
     * 悲观锁查询物料（成本计算并发控制）
     */
    @Select("SELECT * FROM bas_material WHERE id = #{id} FOR UPDATE")
    BasMaterial selectByIdForUpdate(@Param("id") Long id);

    /**
     * 增加锁定库存
     */
    @Update("UPDATE bas_material SET locked_stock = locked_stock + #{qty} WHERE id = #{id}")
    void increaseLockedStock(@Param("id") Long id, @Param("qty") BigDecimal qty);

    /**
     * 减少锁定库存
     */
    @Update("UPDATE bas_material SET locked_stock = locked_stock - #{qty} WHERE id = #{id}")
    void decreaseLockedStock(@Param("id") Long id, @Param("qty") BigDecimal qty);
    /**
     * 统计可用库存 <= 安全库存的预警物料总数
     * 可用库存 = current_stock - locked_stock
     */
    @Select("""
        SELECT COUNT(*) FROM bas_material 
        WHERE (current_stock - locked_stock) <= safety_stock 
        AND is_deleted = 0
    """)
    Long countSafetyWarning();


    /**
     * 区间库存周转率计算
     * 公式：周转率 = 期间出库总量 / 区间平均库存
     */
    @Select("""
        SELECT
            m.id AS materialId,
            m.material_name AS materialName,
            IFNULL(avg_stock.avg_num, 0) AS avgStock,
            IFNULL(out_record.total_out, 0) AS outQty,
            CASE
                WHEN IFNULL(avg_stock.avg_num, 0) <= 0 THEN 0
                ELSE ROUND(IFNULL(out_record.total_out, 0) / avg_stock.avg_num, 4)
            END AS turnoverRate
        FROM bas_material m
        -- 区间平均库存
        LEFT JOIN (
            SELECT material_id, AVG(qty_after) avg_num
            FROM inv_stock_record
            WHERE DATE(create_time) BETWEEN #{start} AND #{end}
            GROUP BY material_id
        ) avg_stock ON m.id = avg_stock.material_id
        -- 区间总出库数量（销售/生产出库）
        LEFT JOIN (
            SELECT material_id, SUM(ABS(qty_change)) total_out
            FROM inv_stock_record
            WHERE DATE(create_time) BETWEEN #{start} AND #{end}
              AND record_type IN (2,4)
            GROUP BY material_id
        ) out_record ON m.id = out_record.material_id
        WHERE m.is_deleted = 0
        ORDER BY turnoverRate DESC
    """)
    List<InventoryTurnoverVO.Item> calcTurnoverRate(
            @Param("start") LocalDate startDate,
            @Param("end") LocalDate endDate
    );

    @Select("""
    SELECT
        m.id materialId,
        m.material_code materialCode,
        m.material_name materialName,
        m.spec,
        m.unit,
        m.material_type materialType,
        m.current_stock currentStock,
        m.locked_stock lockedStock,
        m.available_stock availableStock,
        m.safety_stock safetyStock,
        m.avg_cost avgCost,
        IFNULL(inSum.inQty,0) totalInQty,
        IFNULL(outSum.outQty,0) totalOutQty
    FROM bas_material m
    LEFT JOIN (
        SELECT material_id, SUM(qty_change) inQty
        FROM inv_stock_record
        WHERE record_type IN (1,3)
        GROUP BY material_id
    ) inSum ON m.id = inSum.material_id
    LEFT JOIN (
        SELECT material_id, ABS(SUM(qty_change)) outQty
        FROM inv_stock_record
        WHERE record_type IN (2,4)
        GROUP BY material_id
    ) outSum ON m.id = outSum.material_id
    WHERE m.is_deleted = 0
""")
    List<MaterialStockSummaryVO> selectStockSummary(@Param("dto") MaterialPageDTO dto);
}