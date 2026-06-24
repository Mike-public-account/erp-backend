package com.erp.module.sales.dto;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesShipDTO {
    @NotNull(message = "销售单ID不能为空")
    private Long orderId;
    private String logisticsCompany;
    private String trackingNo;
    private BigDecimal freightAmount;
    @NotEmpty(message = "出库明细不能为空")
    private List<ShipItem> itemList;

    @Data
    public static class ShipItem {
        @NotNull
        private Long itemId;
        @NotNull
        private BigDecimal shipQty;
    }
}