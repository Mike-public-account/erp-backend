package com.erp.module.sales.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.DataScope;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.sales.dto.*;
import com.erp.module.sales.service.SalesOrderService;
import com.erp.module.sales.vo.SalOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import com.erp.module.system.util.LoginUserUtil;
import com.erp.common.utils.ExcelUtil;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/sales/orders")
@RequiredArgsConstructor
public class SalesOrderController {
    private final SalesOrderService salesOrderService;
    private final LoginUserUtil loginUserUtil;
    @GetMapping
    @RequirePermission("sales:order:list")
    @DataScope(alias = "o", userIdColumn = "creator_id")
    public R<Page<SalOrderVO>> page(@Valid SalesOrderPageDTO dto) {
        return R.ok(salesOrderService.pageList(dto));
    }
    
    @GetMapping("/{id}")
    @RequirePermission("sales:order:list")
    @DataScope
    public R<SalOrderVO> detail(@PathVariable Long id) {
        return R.ok(salesOrderService.getDetail(id));
    }


    
    @PutMapping("/{id}/cancel")
    @RequirePermission("sales:order:cancel")
    public R<Void> cancel(@PathVariable Long id) {
        salesOrderService.cancelOrder(id);
        return R.ok();
    }

    
    @PostMapping("/import")
    @RequirePermission("sales:order:import")
    public R<Void> importFile(@RequestParam MultipartFile file) throws Exception {
        salesOrderService.importExcel(file);
        return R.ok();
    }

    // 1. 创建销售单
    @PostMapping
    @RequirePermission("sales:order:add")
    @OperationLog(module = "销售管理", operation = "新建销售单")
    public R<Long> create(@RequestBody @Valid SalesOrderSaveDTO dto, HttpServletRequest request) {
        Long userId = loginUserUtil.getLoginUserId(request);
        Long orderId = salesOrderService.createOrder(dto, userId);
        return R.ok(orderId);
    }

    // 2. 发货出库
    @PutMapping("/{id}/ship")
    @RequirePermission("sales:order:ship")
    @OperationLog(module = "销售管理", operation = "销售单发货出库")
    public R<Void> ship(@PathVariable Long id, @RequestParam Long warehouseId) {
        salesOrderService.shipGoods(id, warehouseId);
        return R.ok();
    }

    // 4. 收款核销
    @PutMapping("/{id}/payment")
    @RequirePermission("sales:order:payment")
    @OperationLog(module = "销售管理", operation = "销售收款核销")
    public R<Void> payment(@PathVariable Long id, @RequestBody @Valid SalesPaymentDTO dto) {
        dto.setOrderId(id);
        salesOrderService.receivePayment(dto);
        return R.ok();
    }

    // 5. 账期预警分页
    @GetMapping("/credit-warning")
    @RequirePermission("sales:order:list")
    public R<Page<SalOrderVO>> creditWarning(SalesOrderPageDTO dto) {
        return R.ok(salesOrderService.listCreditWarning(dto));
    }

    // 6. 销售导出
    @GetMapping("/export")
    @RequirePermission("sales:order:export")
    public void export(SalesOrderPageDTO dto, HttpServletResponse response) throws Exception {
        List<SalOrderVO> dataList = salesOrderService.exportExcel(dto,response);
        ExcelUtil.export(dataList, SalOrderVO.class, "销售订单", response);
    }

    // 7. 单独查询单订单毛利
    @GetMapping("/{id}/gross")
    @RequirePermission("sales:order:list")
    public R<BigDecimal> getGross(@PathVariable Long id) {
        BigDecimal gross = salesOrderService.calcSingleOrderGross(id);
        return R.ok(gross);
    }
}