package com.erp.module.purchase.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.module.purchase.dto.PurAuditDTO;
import com.erp.module.purchase.entity.PurOrder;
import com.erp.module.purchase.entity.PurOrderAudit;
import com.erp.module.purchase.mapper.PurOrderAuditMapper;
import com.erp.module.purchase.mapper.PurOrderMapper;
import com.erp.module.purchase.service.PurAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurAuditServiceImpl extends ServiceImpl<PurOrderAuditMapper, PurOrderAudit>
        implements PurAuditService {
    private final PurOrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(PurAuditDTO dto) {
        PurOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) throw new BusinessException("采购单不存在");
        if (!order.getOrderStatus().equals(2)) throw new BusinessException("仅待审批单据可审核");

        // 保存审批记录
        PurOrderAudit audit = new PurOrderAudit();
        audit.setOrderId(dto.getOrderId());
        audit.setAuditorId(dto.getAuditorId());
        audit.setAuditStatus(dto.getAuditStatus());
        audit.setAuditRemark(dto.getAuditRemark());
        audit.setAuditTime(LocalDateTime.now());
        save(audit);

        // 更新单据状态
        if (dto.getAuditStatus() == 1) {
            order.setOrderStatus(3);
        } else {
            order.setOrderStatus(6);
        }
        order.setApproverId(dto.getAuditorId());
        order.setApproveTime(LocalDateTime.now());
        order.setApproveRemark(dto.getAuditRemark());
        orderMapper.updateById(order);
    }

    @Override
    public List<PurOrderAudit> getAuditList(Long orderId) {
        return baseMapper.selectByOrderId(orderId);
    }
}