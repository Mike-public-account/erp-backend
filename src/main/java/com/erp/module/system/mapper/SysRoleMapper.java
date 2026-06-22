package com.erp.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    /** 根据用户id查询绑定角色 */
    List<SysRole> selectRoleByUserId(@Param("userId") Long userId);
}