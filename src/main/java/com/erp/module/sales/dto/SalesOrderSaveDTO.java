package com.erp.module.sales.dto;
import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesOrderSaveDTO {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    @NotNull(message = "发货仓库不能为空")
    private Long warehouseId;
    private BigDecimal discountAmount;
    private String remark;
    @NotEmpty(message = "销售明细不能为空")
    @Valid
    private List<Item> itemList;

    @Data
    public static class Item {
        @NotNull(message = "成品物料ID不能为空")
        private Long materialId;
        @NotNull(message = "销售数量不能为空")
        private BigDecimal planQty;
        @NotNull(message = "销售单价不能为空")
        private BigDecimal unitPrice;
        private BigDecimal taxRate;
        private String remark;
    }
}