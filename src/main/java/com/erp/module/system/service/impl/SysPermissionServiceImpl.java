package com.erp.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.module.system.entity.SysPermission;
import com.erp.module.system.mapper.SysPermissionMapper;
import com.erp.module.system.service.SysPermissionService;
import com.erp.module.system.vo.PermissionTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<PermissionTreeVO> getPermissionTree() {
        // 查询所有权限，按排序号升序
        List<SysPermission> allPermList = this.list(new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getSortOrder));
        // 实体转VO
        List<PermissionTreeVO> allTreeVO = allPermList.stream()
                .map(perm -> BeanUtil.copyProperties(perm, PermissionTreeVO.class))
                .collect(Collectors.toList());
        // 递归组装树形，根节点parentId=0
        return buildChildrenTree(allTreeVO, 0L);
    }

    /**
     * 递归构建子节点
     */
    private List<PermissionTreeVO> buildChildrenTree(List<PermissionTreeVO> all, Long parentId) {
        return all.stream()
                .filter(item -> item.getParentId().equals(parentId))
                .peek(item -> item.setChildren(buildChildrenTree(all, item.getId())))
                .collect(Collectors.toList());
    }
}