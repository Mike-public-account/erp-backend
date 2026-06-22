package com.erp.module.system.controller;

import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.system.dto.LoginDTO;
import com.erp.module.system.dto.UserPageDTO;
import com.erp.module.system.dto.UserSaveDTO;
import com.erp.module.system.service.SysUserService;
import com.erp.module.system.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/system/users")
public class UserController {
    @Resource
    private SysUserService sysUserService;

    /** 用户分页列表 */
    @GetMapping
    @RequirePermission("system:user:list")
    @OperationLog(module = "用户管理", operation = "查询用户列表")
    public R<Page<UserVO>> page(@Valid UserPageDTO dto) {
        return R.ok(sysUserService.getUserPage(dto));
    }

    /** 新增用户 */
    @PostMapping
    @RequirePermission("system:user:add")
    @OperationLog(module = "用户管理", operation = "新增用户")
    public R<Void> add(@RequestBody @Valid UserSaveDTO dto) {
        sysUserService.addUser(dto);
        return R.ok();
    }

    /** 修改用户 */
    @PutMapping("/{id}")
    @RequirePermission("system:user:edit")
    @OperationLog(module = "用户管理", operation = "编辑用户")
    public R<Void> edit(@PathVariable Long id, @RequestBody @Valid UserSaveDTO dto) {
        sysUserService.editUser(id, dto);
        return R.ok();
    }

    /** 删除用户（逻辑删除） */
    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    @OperationLog(module = "用户管理", operation = "删除用户")
    public R<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return R.ok();
    }

    /** 用户详情 */
    @GetMapping("/{id}")
    public R<UserVO> detail(@PathVariable Long id) {
        return R.ok(sysUserService.getUserInfo(id));
    }

    /** 用户分配角色 */
    @PostMapping("/{id}/assign-roles")
    @RequirePermission("system:user:assign")
    @OperationLog(module = "用户管理", operation = "分配角色")
    public R<Void> assignRole(@PathVariable Long id, @RequestBody List<Long> roleIdList) {
        sysUserService.assignRoles(id, roleIdList);
        return R.ok();
    }

    /** 重置密码 */
    @PostMapping("/{id}/reset-password")
    @RequirePermission("system:user:reset")
    @OperationLog(module = "用户管理", operation = "重置用户密码")
    public R<String> resetPwd(@PathVariable Long id) {
        String newPwd = sysUserService.resetPassword(id);
        return R.ok(newPwd);
    }
}