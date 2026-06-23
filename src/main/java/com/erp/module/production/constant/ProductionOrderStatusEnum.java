package com.erp.module.production.constant;

import lombok.Getter;

@Getter
public enum ProductionOrderStatusEnum {
    DRAFT(0, "草稿"),
    PENDING_PRODUCE(1, "待生产"),
    PRODUCING(2, "生产中"),
    PART_FINISH(3, "部分完工"),
    ALL_FINISH(4, "全部完工"),
    CANCEL(9, "已取消");

    private final Integer code;
    private final String text;

    ProductionOrderStatusEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}