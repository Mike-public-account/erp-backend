package com.erp.module.purchase.job;

import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.purchase.dto.PurchaseOrderDTO;
import com.erp.module.purchase.dto.PurchasePageDTO;
import com.erp.module.purchase.service.PurchaseOrderService;
import com.erp.module.purchase.vo.PurchaseOrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/purchase/orders")
public class PurchaseOrderController {
    @Resource
    private PurchaseOrderService purchaseOrderService;

    /** 采购单分页查询 */
    @GetMapping
    @RequirePermission("purchase:order:list")
    public R<Page<PurchaseOrderVO>> page(PurchasePageDTO dto) {
        return R.ok(purchaseOrderService.pageList(dto));
    }

    /** 新建采购单（自动计算到货时间、库存预占） */
    @PostMapping
    @RequirePermission("purchase:order:add")
    @OperationLog(module = "采购管理", operation = "创建采购单")
    public R<Long> create(@RequestBody @Valid PurchaseOrderDTO dto) {
        Long orderId = purchaseOrderService.createOrder(dto);
        return R.ok(orderId);
    }

    /** 单据详情 */
    @GetMapping("/{id}")
    public R<PurchaseOrderVO> detail(@PathVariable Long id) {
        return R.ok(purchaseOrderService.getDetail(id));
    }

    /** 提交审批 */
    @PutMapping("/{id}/submit")
    @RequirePermission("purchase:order:submit")
    @OperationLog(module = "采购管理", operation = "提交采购单审批")
    public R<Void> submit(@PathVariable Long id) {
        purchaseOrderService.submitAudit(id);
        return R.ok();
    }

    /** 财务审批/驳回 */
    @PutMapping("/{id}/approve")
    @RequirePermission("purchase:order:audit")
    @OperationLog(module = "采购管理", operation = "审批采购单")
    public R<Void> approve(@PathVariable Long id, @RequestParam Integer pass, @RequestParam String remark) {
        purchaseOrderService.auditOrder(id, pass, remark);
        return R.ok();
    }

    /** 仓库入库确认（触发加权成本更新） */
    @PutMapping("/{id}/receipt")
    @RequirePermission("purchase:order:receipt")
    @OperationLog(module = "采购管理", operation = "采购入库")
    public R<Void> receipt(@PathVariable Long id) {
        purchaseOrderService.receiptStock(id);
        return R.ok();
    }

    /** 取消采购单，释放预占库存 */
    @PutMapping("/{id}/cancel")
    @RequirePermission("purchase:order:cancel")
    @OperationLog(module = "采购管理", operation = "取消采购单")
    public R<Void> cancel(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return R.ok();
    }

    /** 查询超时待处理采购单 */
    @GetMapping("/timeout-preview")
    public R<Page<PurchaseOrderVO>> timeoutList(PurchasePageDTO dto) {
        return R.ok(purchaseOrderService.getTimeoutOrder(dto));
    }
}