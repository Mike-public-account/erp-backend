package com.erp.module.system.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
public class RolePageDTO {
    @Min(value = 1, message = "页码不能小于1")
    private Long pageNum = 1L;
    @Range(min = 1, max = 100, message = "每页条数1-100")
    private Long pageSize = 10L;
    private String keyword;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}