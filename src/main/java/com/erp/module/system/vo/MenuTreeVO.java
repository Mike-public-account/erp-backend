package com.erp.module.system.vo;

import lombok.Data;
import java.util.List;

@Data
public class MenuTreeVO {
    private Long id;
    private Long parentId;
    private String permName;
    private String permCode;
    private String path;
    private String icon;
    private List<MenuTreeVO> children;
}