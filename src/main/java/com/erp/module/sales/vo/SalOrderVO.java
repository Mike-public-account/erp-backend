package com.erp.module.sales.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalOrderVO {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Long warehouseId;
    private String warehouseName;
    private Integer orderStatus;
    private String statusText;
    private Integer paymentStatus;
    private String payText;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;

    // ========== 新增 毛利核算字段 ==========
    /** 订单总成本（所有商品加权出库成本合计） */
    private BigDecimal totalCost;
    /** 订单毛利 = 实际金额 - 总成本 */
    private BigDecimal grossProfit;
    /** 毛利率（百分比，如0.15代表15%） */
    private BigDecimal grossRate;

    // ========== 新增 应收/账期预警字段 ==========
    /** 客户约定账期天数（来自客户档案） */
    private Integer creditDay;
    /** 已收款总额 */
    private BigDecimal receivedAmount;
    /** 待收应收款 = 实际金额 - 已收款 */
    private BigDecimal unPayAmount;
    /** 单据出库日期（判断账期基准） */
    private LocalDate shipDate;
    /** 是否超账期 0=未超期 1=已超期 */
    private Integer isOverCredit;
    /** 超账期天数（超期时展示） */
    private Integer overCreditDay;

    // ========== 新增 库存预占相关 ==========
    /** 是否存在未释放预出库库存 0=无 1=有 */
    private Integer hasPreOccupyStock;

    private Long salesmanId;
    private String salesmanName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<SalOrderItemVO> itemList;
}