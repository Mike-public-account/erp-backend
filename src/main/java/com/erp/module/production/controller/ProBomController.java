package com.erp.module.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.production.dto.BomPageDTO;
import com.erp.module.production.dto.BomSaveDTO;
import com.erp.module.production.service.ProBomService;
import com.erp.module.production.vo.BomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/production/bom")
@RequiredArgsConstructor
public class ProBomController {
    private final ProBomService proBomService;

    @GetMapping
    @RequirePermission("production:bom:list")
    public R<Page<BomVO>> page(@Valid BomPageDTO dto) {
        return R.ok(proBomService.bomPage(dto));
    }

    @GetMapping("/{id}")
    @RequirePermission("production:bom:list")
    public R<BomVO> detail(@PathVariable Long id) {
        return R.ok(proBomService.getBomInfo(id));
    }

    @PostMapping
    @RequirePermission("production:bom:add")
    @OperationLog(module = "生产管理", operation = "新增BOM配方")
    public R<Void> add(@RequestBody @Valid BomSaveDTO dto) {
        proBomService.saveBom(dto);
        return R.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("production:bom:edit")
    @OperationLog(module = "生产管理", operation = "修改BOM配方")
    public R<Void> update(@PathVariable Long id, @RequestBody @Valid BomSaveDTO dto) {
        proBomService.updateBom(id, dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("production:bom:delete")
    @OperationLog(module = "生产管理", operation = "删除BOM配方")
    public R<Void> delete(@PathVariable Long id) {
        proBomService.deleteBom(id);
        return R.ok();
    }

    @GetMapping("/product/{productId}")
    @RequirePermission("production:bom:list")
    public R<List<BomVO>> getByProduct(@PathVariable Long productId) {
        return R.ok(proBomService.getBomByProductId(productId));
    }

    @GetMapping("/{productId}/calc-cost")
    public R<BigDecimal> calcBomCost(@PathVariable Long productId) {
        return R.ok(proBomService.calcProductBomCost(productId));
    }
}