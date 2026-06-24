package com.erp.module.purchase.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pur_order_audit")
public class PurOrderAudit {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 采购单ID
    private Long orderId;
    // 审批人ID
    private Long auditorId;
    // 审批状态 1通过 2驳回
    private Integer auditStatus;
    // 审批意见
    private String auditRemark;
    // 审批时间
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
}