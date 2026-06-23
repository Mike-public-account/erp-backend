package com.erp.module.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.base.entity.BasMaterial;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.math.BigDecimal;

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
}