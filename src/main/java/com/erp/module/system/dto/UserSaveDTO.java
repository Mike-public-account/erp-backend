package com.erp.module.system.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户新增/修改入参
 */
@Data
public class UserSaveDTO {
    @NotBlank(message = "登录用户名不能为空")
    private String username;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String password;

    private String phone;

    private String email;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    private Integer status;
}