package com.erp.common.interceptor;

import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "erp:user:token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(GlobalConstant.UNAUTHORIZED, "未登录，请先登录");
        }
        String token = authHeader.replace("Bearer ", "");
        Long userId;
        try {
            userId = JwtUtil.getUserId(token);
        } catch (Exception e) {
            throw new BusinessException(GlobalConstant.UNAUTHORIZED, "Token无效");
        }
        String redisKey = TOKEN_PREFIX + userId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            throw new BusinessException(GlobalConstant.UNAUTHORIZED, "Token已过期，请重新登录");
        }
        // 存入request供后续使用
        request.setAttribute("loginUserId", userId);
        return true;
    }
}