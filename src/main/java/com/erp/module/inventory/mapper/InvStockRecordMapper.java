package com.erp.module.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.inventory.entity.InvStockRecord;
import com.erp.module.inventory.entity.InvPreoccupy;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InvStockRecordMapper extends BaseMapper<InvStockRecord> {

    /** 悲观锁查询物料（入库加权成本计算用） */
    @Select("SELECT * FROM bas_material WHERE id = #{materialId} FOR UPDATE")
    BasMaterial selectMaterialByIdForUpdate(@Param("materialId") Long materialId);

    /** 查询超时采购预占记录 */
    List<InvPreoccupy> selectTimeoutPreoccupy(@Param("now") LocalDateTime now, @Param("preoccupyType") Integer preoccupyType);

    /** 批量扣减物料锁定库存 */
    @Update("UPDATE bas_material SET locked_stock = locked_stock - #{qty} WHERE id = #{materialId}")
    int decreaseLockedStock(@Param("materialId") Long materialId, @Param("qty") BigDecimal qty);
}