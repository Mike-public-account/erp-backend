package com.erp.module.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private LocalDateTime createTime;
}