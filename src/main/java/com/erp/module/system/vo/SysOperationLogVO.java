package com.erp.module.system.vo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysOperationLogVO {
    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String operation;
    private String method;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private Integer responseCode;
    private Integer elapsedTime;
    private String ipAddress;
    private String errorMsg;
    private LocalDateTime createTime;
}