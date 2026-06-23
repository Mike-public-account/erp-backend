package com.erp.module.system.vo;
import lombok.Data;

@Data
public class SysUserRoleVO {
    private Long id;
    private Long userId;
    private Long roleId;
    private String roleName;
}