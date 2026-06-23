package com.erp.module.system.vo;
import lombok.Data;

@Data
public class SysPermissionVO {
    private Long id;
    private Long parentId;
    private String permCode;
    private String permName;
    private Integer permType;
    private String path;
    private String icon;
    private Integer sortOrder;
}