package com.erp.module.purchase.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderVO {
    private Long id;
    /** 采购单号 */
    private String orderNo;

    private Long supplierId;
    /** 供应商名称（扩展字段，前端展示） */
    private String supplierName;

    private Long warehouseId;
    /** 仓库名称 */
    private String warehouseName;

    /** 单据状态编码 */
    private Integer orderStatus;
    /** 状态中文文本 */
    private String statusText;

    /** 单据总金额 */
    private BigDecimal totalAmount;

    /** 预估到货时间 */
    private LocalDateTime estimatedArrivalTime;
    /** 超时截止时间 */
    private LocalDateTime timeoutTime;
    /** 实际入库完成时间 */
    private LocalDateTime actualArrivalTime;

    /** 制单人id/名称 */
    private Long purchaserId;
    private String purchaserName;

    /** 审批人 */
    private Long approverId;
    private String approveName;
    private LocalDateTime approveTime;
    private String approveRemark;

    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 采购明细集合 */
    private List<PurOrderItemVO> itemList;
}