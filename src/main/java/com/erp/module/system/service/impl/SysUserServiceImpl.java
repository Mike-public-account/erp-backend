package com.erp.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.JwtUtil;
import com.erp.common.utils.Md5Util;
import com.erp.module.system.dto.LoginDTO;
import com.erp.module.system.dto.UserPageDTO;
import com.erp.module.system.dto.UserSaveDTO;
import com.erp.module.system.entity.SysUser;
import com.erp.module.system.entity.SysUserRole;
import com.erp.module.system.mapper.SysUserMapper;
import com.erp.module.system.mapper.SysUserRoleMapper;
import com.erp.module.system.service.SysRoleService;
import com.erp.module.system.service.SysUserService;
import com.erp.module.system.vo.MenuTreeVO;
import com.erp.module.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleService roleService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_PREFIX = "erp:user:token:";
    private static final long TOKEN_DAY = 7;

    @Override
    public Page<UserVO> getUserPage(UserPageDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getIsDeleted, GlobalConstant.NOT_DELETE);
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(SysUser::getUsername, dto.getKeyword())
                    .or().like(SysUser::getRealName, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, dto.getStatus());
        }
        Page<SysUser> entityPage = userMapper.selectPage(page, wrapper);
        // 此处自行封装 entity -> UserVO
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserSaveDTO dto) {
        String salt = Md5Util.generateSalt();
        String encryptPwd = Md5Util.encrypt(dto.getPassword(), salt);
        SysUser user = new SysUser();
        // BeanUtils.copyProperties(dto, user);
        user.setSalt(salt);
        user.setPassword(encryptPwd);
        userMapper.insert(user);
        // 分配角色
        assignRoles(user.getId(), List.of(dto.getRoleId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUser(Long id, UserSaveDTO dto) {
        SysUser user = this.getById(id);
        if (user == null || user.getIsDeleted().equals(GlobalConstant.DELETED)) {
            throw new BusinessException(400, "用户不存在");
        }
        // BeanUtils.copyProperties(dto, user, "password", "salt");
        this.updateById(user);
        // 更新角色关联
        assignRoles(id, List.of(dto.getRoleId()));
    }

    @Override
    public UserVO getUserInfo(Long id) {
        SysUser user = this.getById(id);
        // 封装VO返回
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        this.removeById(id);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, GlobalConstant.NOT_DELETE);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Set<String> getUserPermSet(Long userId) {
        // 从Redis/数据库查询用户权限码集合
        return null;
    }

    // ===================== 缺失方法实现 =====================
    /** 分配角色 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIdList) {
        // 先删除该用户所有旧角色关联
        LambdaQueryWrapper<SysUserRole> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(delWrapper);
        // 批量新增
        for (Long rid : roleIdList) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(rid);
            userRoleMapper.insert(ur);
        }
        // 清除权限缓存
        redisTemplate.delete("erp:user:perm:" + userId);
    }

    /** 重置密码 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(Long userId) {
        SysUser user = this.getById(userId);
        String tempPwd = UUID.randomUUID().toString().substring(0, 8);
        String newSalt = Md5Util.generateSalt();
        user.setSalt(newSalt);
        user.setPassword(Md5Util.encrypt(tempPwd, newSalt));
        this.updateById(user);
        // 踢下线，清除token
        redisTemplate.delete(TOKEN_PREFIX + userId);
        return tempPwd;
    }

    /** 登录逻辑 */
    @Override
    public String login(LoginDTO dto) {
        SysUser user = getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        boolean match = Md5Util.match(dto.getPassword(), user.getSalt(), user.getPassword());
        if (!match) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已禁用");
        }
        // 更新最后登录信息
        // user.setLastLoginIp(IP);
        // user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
        // 生成token存入Redis
        String token = JwtUtil.createToken(user.getId());
        redisTemplate.opsForValue().set(TOKEN_PREFIX + user.getId(), token, TOKEN_DAY, TimeUnit.DAYS);
        return token;
    }

    /** 获取用户菜单树 */
    @Override
    public List<MenuTreeVO> getUserMenuTree(Long userId) {
        // 根据用户角色查询菜单权限，组装树形
        return new ArrayList<>();
    }
}