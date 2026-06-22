package com.erp.module.system.controller;

import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.system.dto.RoleSaveDTO;
import com.erp.module.system.dto.UserPageDTO;
import com.erp.module.system.service.SysRoleService;
import com.erp.module.system.vo.RoleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/system/roles")
public class RoleController {
    @Resource
    private SysRoleService sysRoleService;

    @GetMapping
    @RequirePermission("system:role:list")
    public R<Page<RoleVO>> page(UserPageDTO dto) {
        return R.ok(sysRoleService.getRolePage(dto));
    }

    @PostMapping
    @RequirePermission("system:role:add")
    @OperationLog(module = "角色管理", operation = "新增角色")
    public R<Void> add(@RequestBody @Valid RoleSaveDTO dto) {
        sysRoleService.addRole(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    @OperationLog(module = "角色管理", operation = "删除角色")
    public R<Void> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return R.ok();
    }

    /** 角色分配权限 */
    @PutMapping("/{id}/permissions")
    @RequirePermission("system:role:assign")
    @OperationLog(module = "角色管理", operation = "分配权限")
    public R<Void> assignPerm(@PathVariable Long id, @RequestBody List<Long> permIds) {
        sysRoleService.assignPermission(id, permIds);
        return R.ok();
    }

    /** 下拉所有角色（新增用户使用） */
    @GetMapping("/all")
    public R<List<RoleVO>> all() {
        return R.ok(sysRoleService.listAllRole());
    }
}