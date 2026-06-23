package com.erp.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.system.entity.SysPermission;
import com.erp.module.system.vo.PermissionTreeVO;
import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {
    /**
     * 构建完整权限树
     */
    List<PermissionTreeVO> getPermissionTree();
}