package com.erp.common.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前登录用户工具类
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户ID
     * @return userId
     */
    public static Long getUserId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return null;
        }
        return Long.valueOf(userIdObj.toString());
    }

    /**
     * 获取当前请求对象
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}