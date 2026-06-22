package com.erp.module.system.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 用户分页查询DTO（所有模块分页模板统一）
 */
@Data
public class UserPageDTO {
    @Min(value = 1, message = "页码不能小于1")
    private Long pageNum = 1L;

    @Range(min = 1, max = 100, message = "每页条数范围1-100")
    private Long pageSize = 10L;

    // 模糊关键词
    private String keyword;
    // 状态筛选
    private Integer status;
    // 创建时间区间
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}