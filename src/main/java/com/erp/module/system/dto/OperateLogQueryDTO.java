package com.erp.module.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志分页查询条件
 */
@Data
public class OperateLogQueryDTO {
    /** 页码 */
    private Long pageNum = 1L;
    /** 每页条数 */
    private Long pageSize = 10L;
    /** 操作人用户名模糊 */
    private String username;
    /** 模块名称模糊 */
    private String module;
    /** 开始时间 */
    private LocalDateTime start;
    /** 结束时间（新增缺失字段） */
    private LocalDateTime end;
}