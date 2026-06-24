package com.erp.module.purchase.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderDTO {
    /** 主键id，编辑时传，新增不传 */
    private Long id;

    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    @NotNull(message = "入库仓库不能为空")
    private Long warehouseId;

    private Long purchaserId;

    private String remark;

    /** 采购明细列表 */
    @NotEmpty(message = "采购明细不能为空")
    @Valid // 嵌套校验明细内部字段
    private List<Item> itemList;

    @Data
    public static class Item {
        @NotNull(message = "物料id不能为空")
        private Long materialId;

        @NotNull(message = "采购数量不能为空")
        private BigDecimal planQty;

        @NotNull(message = "采购单价不能为空")
        private BigDecimal unitPrice;

        private BigDecimal taxRate;

        private String remark;
    }
}