package com.erp.module.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.inventory.entity.InvPreoccupy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InvPreoccupyMapper extends BaseMapper<InvPreoccupy> {
    List<InvPreoccupy> selectByRefId(@Param("refId") Long refId);
}