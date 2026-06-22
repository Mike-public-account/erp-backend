package com.erp.module.system.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RoleSaveDTO {
    /** 角色编码 唯一 */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /** 角色描述 */
    private String description;
}