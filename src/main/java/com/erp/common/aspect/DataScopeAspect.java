package com.erp.common.aspect;

import com.erp.common.annotation.DataScope;
import com.erp.common.utils.DataScopeContext;
import com.erp.module.system.util.LoginUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class DataScopeAspect {

    @Resource
    private LoginUserUtil loginUserUtil;

    @Pointcut("@annotation(com.erp.common.annotation.DataScope)")
    public void dataScopePointCut() {}

    @Around("dataScopePointCut() && @annotation(dataScope)")
    public Object doAround(ProceedingJoinPoint point, DataScope dataScope) throws Throwable {
        // 获取当前request
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletAttr = (ServletRequestAttributes) attr;
        HttpServletRequest request = servletAttr.getRequest();

        try {
            Long loginUserId = loginUserUtil.getLoginUserId(request);
            boolean isSuperAdmin = loginUserUtil.isSuperAdmin(request);
            DataScopeContext.setUserId(loginUserId);
            DataScopeContext.setAdmin(isSuperAdmin);
            return point.proceed();
        } finally {
            DataScopeContext.clear();
        }
    }
}