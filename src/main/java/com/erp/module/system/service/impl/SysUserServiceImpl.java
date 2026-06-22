package com.erp.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.module.system.dto.UserPageDTO;
import com.erp.module.system.dto.UserSaveDTO;
import com.erp.module.system.entity.SysPermission;
import com.erp.module.system.entity.SysRole;
import com.erp.module.system.entity.SysUser;
import com.erp.module.system.mapper.SysPermissionMapper;
import com.erp.module.system.mapper.SysRoleMapper;
import com.erp.module.system.mapper.SysUserMapper;
import com.erp.module.system.service.SysUserService;
import com.erp.module.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

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

    /**
     * 根据用户名查询用户（登录使用）
     */
    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, GlobalConstant.NOT_DELETE);
        return sysUserMapper.selectOne(wrapper);
    }

    /**
     * 获取用户权限集合（权限拦截器使用）
     * @param userId 用户ID
     * @return 权限标识集合
     */
    @Override
    public Set<String> getUserPermSet(Long userId) {
        String cacheKey = "erp:user:perm:" + userId;
        // 读取缓存
        Set<String> permCache = (Set<String>) redisTemplate.opsForValue().get(cacheKey);
        if (permCache != null) {
            return permCache;
        }

        Set<String> permSet = new HashSet<>();
        // 查询用户绑定角色
        List<SysRole> roleList = sysRoleMapper.selectRoleByUserId(userId);
        if (roleList.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, permSet, 30L, TimeUnit.MINUTES);
            return permSet;
        }

        // 提取角色ID批量查权限
        List<Long> roleIds = roleList.stream()
                .map(SysRole::getId)
                .collect(Collectors.toList());
        List<SysPermission> permList = sysPermissionMapper.selectPermByRoleIds(roleIds);

        // 收集权限标识（字段是permCode，对应getPermCode()）
        permList.forEach(perm -> permSet.add(perm.getPermCode()));

        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, permSet, 30L, TimeUnit.MINUTES);
        return permSet;
    }
}