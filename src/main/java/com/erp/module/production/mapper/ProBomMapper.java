package com.erp.module.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.production.entity.ProBom;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface ProBomMapper extends BaseMapper<ProBom> {
    /** 根据成品id查询所有BOM原料 */
    @Select("SELECT * FROM pro_bom WHERE product_material_id = #{productId} AND is_deleted = 0")
    List<ProBom> selectByProductId(@Param("productId") Long productId);
}