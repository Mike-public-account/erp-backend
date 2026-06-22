package com.erp.module.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.system.dto.UserPageDTO;
import com.erp.module.system.dto.UserSaveDTO;
import com.erp.module.system.entity.SysUser;
import com.erp.module.system.vo.UserVO;

import java.util.Set;

public interface SysUserService extends IService<SysUser> {
    /**
     * 用户分页列表
     */
    Page<UserVO> getUserPage(UserPageDTO dto);

    /**
     * 新增用户
     */
    void addUser(UserSaveDTO dto);

    /**
     * 修改用户
     */
    void editUser(Long id, UserSaveDTO dto);

    /**
     * 用户详情
     */
    UserVO getUserInfo(Long id);

    /**
     * 逻辑删除
     */
    void deleteUser(Long id);
    /**
     * 根据用户名查询用户（登录使用）
     */
    SysUser getUserByUsername(String username);

    /**
     * 获取用户权限集合（权限拦截器使用）
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getUserPermSet(Long userId);
}