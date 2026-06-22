package com.erp.common.constant;

import lombok.Getter;

/**
 * 库存流水记录类型
 */
@Getter
public enum StockRecordTypeEnum {
    PURCHASE_IN(1, "采购入库"),
    PROD_OUT(2, "生产领料出库"),
    PROD_IN(3, "生产完工入库"),
    SALE_OUT(4, "销售出库"),
    PRE_OCCUPY(5, "预占"),
    PRE_OUT(6, "预出库"),
    PRE_RELEASE(7, "预占释放"),
    ADJUST(8, "盘点调整");

    private final Integer code;
    private final String desc;

    StockRecordTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}