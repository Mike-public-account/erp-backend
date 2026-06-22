package com.erp.module.system.vo;

import lombok.Data;
import java.util.List;

@Data
public class PermissionTreeVO {
    private Long id;
    private Long parentId;
    private String permName;
    private String permCode;
    private Integer permType;
    private String path;
    private String icon;
    private Integer sortOrder;
    private List<PermissionTreeVO> children;
}