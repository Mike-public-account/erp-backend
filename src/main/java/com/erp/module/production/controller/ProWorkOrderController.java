package com.erp.module.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.common.utils.ExcelUtil;
import com.erp.module.production.dto.WorkOrderFinishDTO;
import com.erp.module.production.dto.WorkOrderPageDTO;
import com.erp.module.production.dto.WorkOrderPickDTO;
import com.erp.module.production.dto.WorkOrderSaveDTO;
import com.erp.module.production.service.ProWorkOrderService;
import com.erp.module.production.vo.WorkOrderExportVO;
import com.erp.module.production.vo.WorkOrderVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/production/work-order")
public class ProWorkOrderController {
    @Resource
    private ProWorkOrderService workOrderService;

    @GetMapping
    @RequirePermission("production:workorder:list")
    public R<Page<WorkOrderVO>> page(WorkOrderPageDTO dto) {
        return R.ok(workOrderService.workOrderPage(dto));
    }

    @GetMapping("/{id}")
    public R<WorkOrderVO> detail(@PathVariable Long id) {
        return R.ok(workOrderService.getOrderDetail(id));
    }

    @PostMapping
    @RequirePermission("production:workorder:add")
    @OperationLog(module = "生产管理", operation = "创建生产工单")
    public R<Long> create(@RequestBody @Valid WorkOrderSaveDTO dto) {
        Long orderId = workOrderService.createWorkOrder(dto);
        return R.ok(orderId);
    }

    @PostMapping("/pick")
    @RequirePermission("production:workorder:pick")
    @OperationLog(module = "生产管理", operation = "工单原料领料")
    public R<Void> pickMaterial(@RequestBody @Valid WorkOrderPickDTO dto) {
        workOrderService.pickMaterial(dto);
        return R.ok();
    }

    @PostMapping("/finish")
    @RequirePermission("production:workorder:finish")
    @OperationLog(module = "生产管理", operation = "工单完工入库")
    public R<Void> finish(@RequestBody @Valid WorkOrderFinishDTO dto) {
        workOrderService.finishWorkOrder(dto);
        return R.ok();
    }

    @PutMapping("/cancel/{id}")
    @RequirePermission("production:workorder:cancel")
    @OperationLog(module = "生产管理", operation = "取消生产工单")
    public R<Void> cancel(@PathVariable Long id) {
        workOrderService.cancelWorkOrder(id);
        return R.ok();
    }
    @GetMapping("/export")
    @RequirePermission("production:workorder:export")
    @OperationLog(module = "生产管理", operation = "导出生产工单")
    public void export(WorkOrderPageDTO dto, HttpServletResponse response) throws IOException {
        List<WorkOrderExportVO> voList = workOrderService.exportWorkOrder(dto);
        ExcelUtil.export(voList, WorkOrderExportVO.class, "生产工单台账.xlsx", response);
    }
    @PutMapping("/batch/cancel")
    @RequirePermission("production:workorder:cancel")
    @OperationLog(module = "生产管理", operation = "批量取消工单")
    public R<Void> batchCancel(@RequestBody List<Long> orderIdList) {
        workOrderService.batchCancelWorkOrder(orderIdList);
        return R.ok();
    }
}