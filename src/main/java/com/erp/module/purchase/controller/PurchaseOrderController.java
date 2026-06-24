package com.erp.module.purchase.controller;

import cn.hutool.core.bean.BeanUtil;
import com.erp.common.annotation.DataScope;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.common.utils.ExcelUtil;
import com.erp.module.purchase.dto.*;
import com.erp.module.purchase.entity.PurOrderAudit;
import com.erp.module.purchase.service.PurAuditService;
import com.erp.module.purchase.service.PurPrepayService;
import com.erp.module.purchase.service.PurchaseOrderService;
import com.erp.module.purchase.vo.PurOrderAuditVO;
import com.erp.module.purchase.vo.PurPrepayVO;
import com.erp.module.purchase.vo.PurchaseOrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import com.erp.module.system.util.LoginUserUtil;


@RestController
@RequestMapping("/api/v1/purchase/orders")
@RequiredArgsConstructor // lombok自动生成全参构造，无需手动写构造
public class PurchaseOrderController {
    // 业务服务注入
    private final PurchaseOrderService purchaseOrderService;
    private final PurAuditService purAuditService;
    // 新增预付款服务注入
    private final PurPrepayService purPrepayService;
    private final LoginUserUtil loginUserUtil;

    /** 采购单分页查询 */
    @GetMapping
    @RequirePermission("purchase:order:list")
    @DataScope(alias = "p", userIdColumn = "creator_id")
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
    @RequirePermission("purchase:order:list")
    @DataScope
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
    @RequirePermission("purchase:order:list")
    @DataScope(alias = "p", userIdColumn = "creator_id")
    public R<Page<PurchaseOrderVO>> timeoutList(PurchasePageDTO dto) {
        return R.ok(purchaseOrderService.getTimeoutOrder(dto));
    }

    /** 审批采购单（新审批流，带审批记录存储） */
    @PutMapping("/{id}/audit")
    @RequirePermission("purchase:order:audit")
    @OperationLog(module = "采购管理", operation = "审批采购单")
    public R<Void> audit(@PathVariable Long id, @RequestBody @Valid PurAuditDTO dto) {
        dto.setOrderId(id);
        purAuditService.auditOrder(dto);
        return R.ok();
    }

    @GetMapping("/{id}/audit/list")
    @RequirePermission("purchase:order:list")
    @OperationLog(module = "采购管理", operation = "查询审批记录")
    public R<List<PurOrderAuditVO>> getAuditRecord(@PathVariable Long id) {
        List<PurOrderAudit> entityList = purAuditService.getAuditList(id);
        List<PurOrderAuditVO> voList = BeanUtil.copyToList(entityList, PurOrderAuditVO.class);
        return R.ok(voList);
    }

    // ===================== 预付款接口 =====================
    @PostMapping("/prepay")
    @RequirePermission("purchase:order:prepay")
    @OperationLog(module = "采购预付款", operation = "新增预付款单")
    public R<Void> prepay(@RequestBody @Valid PurPrepayDTO dto) {
        purPrepayService.createPrepay(dto);
        return R.ok();
    }

    @PutMapping("/prepay/{id}/writeOff")
    @RequirePermission("purchase:order:writeOff")
    public R<Void> writeOff(@PathVariable Long id,
                            @RequestParam BigDecimal amount,
                            @RequestParam Long itemId) {
        purPrepayService.writeOffPrepay(id, itemId, amount);
        return R.ok();
    }

    @GetMapping("/prepay/page")
    public R<Page<PurPrepayVO>> prepayPage(PurPrepayQueryDTO dto) {
        return R.ok(purPrepayService.page(dto));
    }
    @PostMapping("/import")
    @RequirePermission("purchase:order:import")
    @OperationLog(module = "采购管理", operation = "批量导入采购单")
    public R<Void> importOrder(@RequestParam MultipartFile file, HttpServletRequest request) throws Exception {
        // 在Controller层获取登录用户ID（Controller可拿到request）
        Long loginUserId = loginUserUtil.getLoginUserId(request);
        List<PurOrderImportDTO> list = ExcelUtil.importExcel(file, PurOrderImportDTO.class);
        purchaseOrderService.importOrder(list, loginUserId);
        return R.ok();
    }
}