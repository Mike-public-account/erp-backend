package com.erp.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.module.system.dto.UserPageDTO;
import com.erp.module.system.dto.UserSaveDTO;
import com.erp.module.system.entity.SysUser;
import com.erp.module.system.mapper.SysUserMapper;
import com.erp.module.system.service.SysUserService;
import com.erp.module.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private final SysUserMapper sysUserMapper;

    @Override
    public Page<UserVO> getUserPage(UserPageDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 过滤已删除
        wrapper.eq(SysUser::getIsDeleted, GlobalConstant.NOT_DELETE);
        // 关键词模糊
        if (dto.getKeyword() != null) {
            wrapper.like(SysUser::getUsername, dto.getKeyword())
                    .or()
                    .like(SysUser::getRealName, dto.getKeyword());
        }
        // 状态筛选
        if (dto.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, dto.getStatus());
        }
        Page<SysUser> entityPage = sysUserMapper.selectPage(page, wrapper);
        // 后续自行封装entity转VO
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserSaveDTO dto) {
        // dto转entity、密码加盐逻辑day3实现
        SysUser user = new SysUser();
        // BeanUtils.copyProperties(dto, user);
        sysUserMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUser(Long id, UserSaveDTO dto) {
        SysUser user = this.getById(id);
        // BeanUtils.copyProperties(dto, user);
        sysUserMapper.updateById(user);
    }

    @Override
    public UserVO getUserInfo(Long id) {
        SysUser user = this.getById(id);
        // entity转VO
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        this.removeById(id);
    }
}