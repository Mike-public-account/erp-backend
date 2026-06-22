package com.erp.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.system.entity.SysPermission;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /** 根据角色id集合查询权限 */
    List<SysPermission> selectPermByRoleIds(@Param("roleIds") List<Long> roleIds);
}