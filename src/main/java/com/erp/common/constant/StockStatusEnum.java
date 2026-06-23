package com.erp.common.constant;

import lombok.Getter;

@Getter
public enum StockStatusEnum {
    /** 待入库 */
    PENDING_STOCK(0, "待入库"),
    /** 已入库 */
    STOCKED(1, "已入库"),
    /** 已出库 */
    OUT(2, "已出库");

    private final Integer code;
    private final String text;

    StockStatusEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}