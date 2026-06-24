package com.erp.module.sales.constant;
import lombok.Getter;

@Getter
public enum SalesOrderStatusEnum {
    DRAFT(1, "草稿"),
    PENDING_SHIP(2, "待出库"),
    PART_SHIP(3, "部分出库"),
    ALL_SHIP(4, "全部出库"),
    CANCEL(5, "已取消");

    private final Integer code;
    private final String desc;
    SalesOrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}