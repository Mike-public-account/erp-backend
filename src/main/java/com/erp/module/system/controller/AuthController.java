package com.erp.module.system.controller;

import com.erp.common.annotation.OperationLog;
import com.erp.common.result.R;
import com.erp.common.utils.JwtUtil;
import com.erp.common.utils.Md5Util;
import com.erp.module.system.dto.LoginDTO;
import com.erp.module.system.entity.SysUser;
import com.erp.module.system.service.SysUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private HttpServletRequest request;

    private static final String TOKEN_KEY_PREFIX = "erp:user:token:";
    private static final long TOKEN_EXPIRE = 7;

    @PostMapping("/login")
    @OperationLog(module = "认证模块", operation = "用户登录")
    public R<String> login(@RequestBody LoginDTO dto) {
        // 查询用户
        SysUser user = sysUserService.getUserByUsername(dto.getUsername());
        // 密码校验
        boolean match = Md5Util.match(dto.getPassword(), user.getSalt(), user.getPassword());
        if (!match) {
            return R.fail("用户名或密码错误");
        }
        // 生成token
        String token = JwtUtil.createToken(user.getId());
        // Redis缓存token
        redisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + user.getId(), token, TOKEN_EXPIRE, TimeUnit.DAYS);
        return R.ok(token);
    }

    @PostMapping("/logout")
    @OperationLog(module = "认证模块", operation = "退出登录")
    public R<Void> logout() {
        // 从request获取拦截器存入的登录用户ID
        Long loginUserId = (Long) request.getAttribute("loginUserId");
        redisTemplate.delete(TOKEN_KEY_PREFIX + loginUserId);
        return R.ok();
    }
}