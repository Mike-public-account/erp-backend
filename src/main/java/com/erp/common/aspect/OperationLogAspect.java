package com.erp.common.aspect;

import com.erp.common.annotation.OperationLog;
import com.erp.module.system.entity.SysOperationLog;
import com.erp.module.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final SysOperationLogMapper logMapper;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint jp, OperationLog operationLog) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = getRequest();
        SysOperationLog sysLog = new SysOperationLog();
        sysLog.setModule(operationLog.module());
        sysLog.setOperation(operationLog.operation());
        sysLog.setMethod(jp.getSignature().toShortString());
        sysLog.setRequestUrl(request.getRequestURI());
        sysLog.setRequestMethod(request.getMethod());
        sysLog.setIpAddress(getClientIp(request));

        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) sysLog.setUserId(userId);

        try {
            Object result = jp.proceed();
            sysLog.setResponseCode(200);
            return result;
        } catch (Exception e) {
            sysLog.setResponseCode(500);
            sysLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            sysLog.setElapsedTime((int)(System.currentTimeMillis() - start));
            sysLog.setCreateTime(LocalDateTime.now());
            saveLog(sysLog);
        }
    }

    @Async
    public void saveLog(SysOperationLog log) {
        logMapper.insert(log);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attr != null;
        return attr.getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}