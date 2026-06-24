package com.erp.module.system.util;

import com.erp.common.exception.BusinessException;
import com.erp.common.constant.CacheKey;
import com.erp.common.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class LoginUserUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 从请求头获取当前登录用户ID
     */
    public Long getLoginUserId(HttpServletRequest request) {
        String token = getToken(request);
        Long userId = JwtUtil.getUserId(token);
        // 校验token是否有效
        String userTokenKey = CacheKey.USER_TOKEN + userId;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(userTokenKey))) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        return userId;
    }

    /**
     * 判断当前登录人是否超级管理员
     * 这里简化：实际项目可从Redis读取用户角色判断，此处预留扩展点
     */
    public boolean isSuperAdmin(HttpServletRequest request) {
        Long userId = getLoginUserId(request);
        // 示例：假设ID=1为超级管理员，可替换为查角色
        return userId == 1L;
    }

    /**
     * 提取Header中的Token
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException(401, "未携带登录凭证");
        }
        return header.replace("Bearer ", "");
    }
}