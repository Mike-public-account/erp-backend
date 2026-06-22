package com.erp.common.constant;

import lombok.Getter;

/**
 * 采购/销售/生产工单通用单据状态
 */
@Getter
public enum OrderStatusEnum {
    DRAFT(1, "草稿"),
    AUDIT_PENDING(2, "待审批"),
    AUDIT_PASS(3, "审批通过"),
    PART_COMPLETE(4, "部分完成"),
    ALL_COMPLETE(5, "全部完成"),
    CANCEL(6, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}