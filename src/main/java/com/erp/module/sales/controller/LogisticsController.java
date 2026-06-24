package com.erp.module.sales.controller;
import com.erp.common.result.R;
import com.erp.module.sales.dto.LogisticsReconcileDTO;
import com.erp.module.sales.service.LogisticsService;
import com.erp.module.sales.vo.LogisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales/logistics")
@RequiredArgsConstructor
public class LogisticsController {
    private final LogisticsService logisticsService;

    @GetMapping("/order/{orderId}")
    public R<List<LogisticsVO>> list(@PathVariable Long orderId) {
        return R.ok(logisticsService.getByOrderId(orderId));
    }
    @PostMapping("/reconcile")
    public R<Void> reconcile(@RequestBody @Valid LogisticsReconcileDTO dto) {
        logisticsService.reconcile(dto);
        return R.ok();
    }
}