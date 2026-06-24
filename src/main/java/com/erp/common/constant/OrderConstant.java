package com.erp.common.constant;

/**
 * 订单业务常量（采购/通用订单）
 */
public class OrderConstant {

    // ===================== 运输相关 =====================
    /** 平均运输车速：60km/h */
    public static final double TRANSPORT_SPEED_KM_PER_HOUR = 60.0;

    // ===================== 超时规则 =====================
    /** 到货后订单超时判定时长（分钟） */
    public static final long ORDER_TIMEOUT_MIN = 720L;

    // ===================== 采购单状态 =====================
    /** 草稿 */
    public static final Integer ORDER_STATUS_DRAFT = 1;
    /** 待审批 */
    public static final Integer ORDER_STATUS_PENDING_AUDIT = 2;
    /** 已审批 */
    public static final Integer ORDER_STATUS_AUDITED = 3;
    /** 部分入库 */
    public static final Integer ORDER_STATUS_PART_RECEIPT = 4;
    /** 全部入库 */
    public static final Integer ORDER_STATUS_FULL_RECEIPT = 5;
    /** 驳回 */
    public static final Integer ORDER_STATUS_REJECT = 6;
    /** 已取消 */
    public static final Integer ORDER_STATUS_CANCEL = 7;

    // ===================== 对账状态 =====================
    /** 待确认对账 */
    public static final Integer RECONCILIATION_STATUS_WAIT = 1;
    /** 对账完成 */
    public static final Integer RECONCILIATION_STATUS_FINISH = 2;

    // ===================== 预付款状态 =====================
    /** 未核销 */
    public static final Integer PREPAY_STATUS_UNWRITE_OFF = 0;
    /** 部分核销 */
    public static final Integer PREPAY_STATUS_PART = 1;
    /** 全部核销 */
    public static final Integer PREPAY_STATUS_ALL = 2;

}