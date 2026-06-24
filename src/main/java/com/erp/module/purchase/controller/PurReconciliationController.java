package com.erp.module.purchase.controller;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.purchase.dto.PurReconciliationDTO;
import com.erp.module.purchase.dto.PurReconciliationQueryDTO;
import com.erp.module.purchase.service.PurReconciliationService;
import com.erp.module.purchase.vo.PurReconciliationVO;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/purchase/reconciliation")
@RequiredArgsConstructor
public class PurReconciliationController {
    // 注入变量名 reconciliationService
    private final PurReconciliationService reconciliationService;

    @GetMapping("/page")
    public R<Page<PurReconciliationVO>> page(PurReconciliationQueryDTO dto) {
        return R.ok(reconciliationService.pageList(dto));
    }

    @PostMapping
    @RequirePermission("purchase:reconciliation:add")
    @OperationLog(module = "采购对账", operation = "生成供应商对账单")
    public R<Void> create(@RequestBody @Valid PurReconciliationDTO dto) {
        // 修正：reconciliation → reconciliationService
        reconciliationService.createReconciliation(dto);
        return R.ok();
    }

    @PutMapping("/{id}/confirm")
    @RequirePermission("purchase:reconciliation:confirm")
    @OperationLog(module = "采购对账", operation = "确认对账完成")
    public R<Void> confirm(@PathVariable Long id) {
        reconciliationService.confirmReconciliation(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<PurReconciliationVO> detail(@PathVariable Long id) {
        return R.ok(reconciliationService.getDetail(id));
    }
}