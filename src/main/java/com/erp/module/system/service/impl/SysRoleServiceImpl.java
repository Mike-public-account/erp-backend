package com.erp.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.module.system.dto.RolePageDTO;
import com.erp.module.system.dto.RoleSaveDTO;
import com.erp.module.system.entity.SysRole;
import com.erp.module.system.entity.SysRolePermission;
import com.erp.module.system.mapper.SysRoleMapper;
import com.erp.module.system.mapper.SysRolePermissionMapper;
import com.erp.module.system.service.SysRoleService;
import com.erp.module.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermMapper;

    @Override
    public Page<RoleVO> getRolePage(RolePageDTO dto) {
        Page<SysRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getIsDeleted, GlobalConstant.NOT_DELETE);
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(SysRole::getRoleName, dto.getKeyword())
                    .or().like(SysRole::getRoleCode, dto.getKeyword());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        Page<SysRole> entityPage = roleMapper.selectPage(page, wrapper);

        // entity 转 VO
        List<RoleVO> voList = entityPage.getRecords().stream()
                .map(role -> BeanUtil.copyProperties(role, RoleVO.class))
                .collect(Collectors.toList());

        Page<RoleVO> resultPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleSaveDTO dto) {
        // 判断角色编码唯一
        LambdaQueryWrapper<SysRole> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(SysRole::getRoleCode, dto.getRoleCode());
        SysRole exist = roleMapper.selectOne(checkWrapper);
        if (exist != null) {
            throw new BusinessException(GlobalConstant.PARAM_ERR, "角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanUtil.copyProperties(dto, role);
        roleMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermission(Long roleId, List<Long> permIdList) {
        // 删除原有权限关联
        LambdaQueryWrapper<SysRolePermission> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(SysRolePermission::getRoleId, roleId);
        rolePermMapper.delete(delWrapper);
        // 批量新增
        for (Long permId : permIdList) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            rolePermMapper.insert(rp);
        }
    }

    @Override
    public List<RoleVO> listAllRole() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getIsDeleted, GlobalConstant.NOT_DELETE);
        List<SysRole> list = roleMapper.selectList(wrapper);
        return list.stream()
                .map(role -> BeanUtil.copyProperties(role, RoleVO.class))
                .collect(Collectors.toList());
    }
}