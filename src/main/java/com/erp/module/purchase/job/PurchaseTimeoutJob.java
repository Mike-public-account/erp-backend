package com.erp.module.purchase.job;

import com.erp.common.constant.OrderStatusEnum;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.mapper.InvPreoccupyMapper;
import com.erp.module.inventory.service.StockService;
import com.erp.module.purchase.entity.PurOrder;
import com.erp.module.purchase.mapper.PurOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PurchaseTimeoutJob {

    @Resource
    private InvPreoccupyMapper preoccupyMapper;
    @Resource
    private PurOrderMapper orderMapper;
    @Resource
    private StockService stockService;

    /**
     * 每5分钟扫描一次超时的采购预占，自动释放并取消采购单
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    @Transactional(rollbackFor = Exception.class)
    public void scanTimeout() {
        // 查询已超时且状态为有效的采购预占
        List<InvPreoccupy> timeoutList = preoccupyMapper.selectList(
                new LambdaQueryWrapper<InvPreoccupy>()
                        .eq(InvPreoccupy::getPreoccupyType, 1) // 1采购预占
                        .eq(InvPreoccupy::getPreoccupyStatus, 1) // 1有效
                        .lt(InvPreoccupy::getTimeoutTime, LocalDateTime.now())
        );

        for (InvPreoccupy preoccupy : timeoutList) {
            // 释放预占
            stockService.releasePreoccupyByRef(preoccupy.getRefId(), preoccupy.getRefType());

            // 更新对应采购单为已取消
            PurOrder order = orderMapper.selectById(preoccupy.getRefId());
            if (order != null) {
                order.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
                order.setApproveRemark("系统自动取消：采购预占超时");
                orderMapper.updateById(order);
            }
        }
    }
}