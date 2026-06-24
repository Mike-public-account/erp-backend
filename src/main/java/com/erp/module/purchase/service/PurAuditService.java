package com.erp.module.purchase.service;
import com.erp.module.purchase.dto.PurAuditDTO;
import com.erp.module.purchase.entity.PurOrderAudit;
import java.util.List;

public interface PurAuditService {
    /** 执行审批操作 */
    void auditOrder(PurAuditDTO dto);
    /** 查询订单全部审批记录 */
    List<PurOrderAudit> getAuditList(Long orderId);
}