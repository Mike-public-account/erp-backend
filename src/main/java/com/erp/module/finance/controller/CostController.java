package com.erp.module.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.result.R;
import com.erp.module.finance.dto.CostQueryDTO;
import com.erp.module.finance.service.CostCalcService;
import com.erp.module.finance.vo.CostRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/finance/cost")
@RequiredArgsConstructor
public class CostController {
    private final CostCalcService costCalcService;

    @GetMapping("/realtime")
    public R<BigDecimal> getRealtime(@RequestParam Long productId) {
        return R.ok(costCalcService.getRealtimeCost(productId));
    }

    @GetMapping("/list")
    public R<Page<CostRecordVO>> page(@Valid CostQueryDTO dto) {
        return R.ok(costCalcService.pageCost(dto));
    }

    @PostMapping("/calc-now")
    public R<Void> manualCalc(@RequestParam String period) {
        costCalcService.manualCalc(period);
        return R.ok();
    }
    @GetMapping("/period")
    public R<Page<CostRecordVO>> getPeriodCost(@Valid CostQueryDTO dto) {
        return R.ok(costCalcService.pageCost(dto));
    }
}