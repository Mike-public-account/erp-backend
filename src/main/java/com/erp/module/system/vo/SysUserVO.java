package com.erp.module.system.vo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private String roleName; // 扩展：角色名称，库表无
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    // 剔除 password、salt、isDeleted 敏感内部字段
}