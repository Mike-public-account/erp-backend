package com.erp.module.report.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class InventoryTurnoverVO {
    private List<Item> itemList;

    @Data
    public static class Item {
        private Long materialId;
        private String materialName;
        private BigDecimal avgStock;       // 区间平均库存
        private BigDecimal outQty;         // 期间出库总量
        private BigDecimal turnoverRate;   // 库存周转率
    }
}