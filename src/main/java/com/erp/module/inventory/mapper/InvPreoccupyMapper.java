package com.erp.module.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.inventory.entity.InvPreoccupy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InvPreoccupyMapper extends BaseMapper<InvPreoccupy> {
    List<InvPreoccupy> selectTimeoutPurchasePreoccupy(@Param("now") LocalDateTime now);

    @Select("SELECT * FROM inv_preoccupy " +
            "WHERE ref_type = #{refType} AND ref_id IN #{refIdList} AND preoccupy_status = 1")
    List<InvPreoccupy> selectByRefIds(@Param("refType") String refType, @Param("refIdList") List<Long> refIdList);
}