package com.erp.common.interceptor;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.module.system.service.SysUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RequirePermission annotation = handlerMethod.getMethodAnnotation(RequirePermission.class);
        // 无注解 无需校验
        if (annotation == null) {
            return true;
        }
        String needPerm = annotation.value();
        Long userId = (Long) request.getAttribute("loginUserId");
        // 查询用户权限集合（先查Redis缓存）
        Set<String> userPermSet = sysUserService.getUserPermSet(userId);
        if (!userPermSet.contains(needPerm)) {
            throw new BusinessException(GlobalConstant.FORBIDDEN, "当前账号无操作权限");
        }
        return true;
    }
}