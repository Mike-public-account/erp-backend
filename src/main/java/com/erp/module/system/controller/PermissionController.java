package com.erp.module.system.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.system.service.SysPermissionService;
import com.erp.module.system.vo.PermissionTreeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * 权限菜单控制器
 * 接口地址：/api/v1/system/permissions
 * 功能：查询树形菜单权限（角色分配权限使用）
 */
@RestController
@RequestMapping("/api/v1/system/permissions")
public class PermissionController {

    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 获取全部权限树形结构（父节点+子菜单/按钮）
     * 权限标识：system:perm:list
     * 用于角色分配权限弹窗加载树
     */
    @GetMapping("/tree")
    @RequirePermission("system:perm:list")
    public R<List<PermissionTreeVO>> getPermissionTree() {
        List<PermissionTreeVO> treeList = sysPermissionService.getPermissionTree();
        return R.ok(treeList);
    }
}