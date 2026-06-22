package com.erp.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.erp.common.annotation.OperationLog;
import com.erp.module.system.entity.SysOperationLog;
import com.erp.module.system.mapper.SysOperationLogMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {
    @Resource
    private SysOperationLogMapper operationLogMapper;
    @Resource
    private HttpServletRequest request;

    // 修正：注解变量名与方法参数opLog对应，注解类OperationLog
    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint point, OperationLog opLog) throws Throwable {
        long start = System.currentTimeMillis();
        Long userId = (Long) request.getAttribute("loginUserId");
        SysOperationLog log = new SysOperationLog();
        log.setUserId(userId);
        log.setModule(opLog.module());
        log.setOperation(opLog.operation());
        log.setRequestUrl(request.getRequestURI());
        log.setRequestMethod(request.getMethod());
        log.setRequestParams(JSON.toJSONString(point.getArgs()));
        log.setIpAddress(getIp());
        log.setCreateTime(LocalDateTime.now());
        try {
            Object result = point.proceed();
            log.setResponseCode(200);
            return result;
        } catch (Exception e) {
            log.setResponseCode(500);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.setElapsedTime((int) cost);
            operationLogMapper.insert(log);
        }
    }

    private String getIp() {
        return request.getRemoteAddr();
    }
}