package com.erp.module.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户列表返回VO（剔除密码、盐等敏感字段）
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private String roleName;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}