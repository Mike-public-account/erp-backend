package com.erp.module.inventory.job;


import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.mapper.InvPreoccupyMapper;
import com.erp.module.purchase.mapper.PurOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购预占超时自动释放定时任务
 * 每5分钟扫描一次超时未入库采购预占记录
 */
@Component
@RequiredArgsConstructor
public class PurchaseInventoryTimeoutJob {

    private final InvPreoccupyMapper preoccupyMapper;
    private final BasMaterialMapper materialMapper;
    private final PurOrderMapper purOrderMapper;

    /**
     * 定时执行：每5分钟
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    @Transactional(rollbackFor = Exception.class)
    public void scanTimeoutPreoccupy() {
        LocalDateTime now = LocalDateTime.now();
        // 查询采购类型、有效、已超时预占
        List<InvPreoccupy> timeoutList = preoccupyMapper.selectTimeoutPurchasePreoccupy(now);
        if (timeoutList.isEmpty()) {
            return;
        }
        for (InvPreoccupy pre : timeoutList) {
            // 1. 更新预占记录为已释放
            pre.setPreoccupyStatus(2);
            pre.setReleaseTime(now);
            preoccupyMapper.updateById(pre);

            // 2. 物料锁定库存扣减
            materialMapper.decreaseLockedStock(pre.getMaterialId(), pre.getQty());

            // 3. 对应采购单改为已取消
            purOrderMapper.updateOrderStatusById(pre.getRefId(), 6);
        }
    }
}