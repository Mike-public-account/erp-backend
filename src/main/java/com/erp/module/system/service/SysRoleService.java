package com.erp.module.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.system.dto.RolePageDTO;
import com.erp.module.system.dto.RoleSaveDTO;
import com.erp.module.system.entity.SysRole;
import com.erp.module.system.vo.RoleVO;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    /** 角色分页查询 */
    Page<RoleVO> getRolePage(RolePageDTO dto);

    /** 新增角色 */
    void addRole(RoleSaveDTO dto);

    /** 分配角色权限 */
    void assignPermission(Long roleId, List<Long> permIdList);

    /** 查询全部角色（下拉框） */
    List<RoleVO> listAllRole();
}