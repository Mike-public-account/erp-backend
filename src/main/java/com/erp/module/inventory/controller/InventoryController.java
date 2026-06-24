package com.erp.module.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.result.R;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.annotation.OperationLog;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.inventory.dto.InventoryBatchAdjustDTO;
import com.erp.module.inventory.dto.StockAdjustDTO;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.dto.StockQueryDTO;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.entity.InvStockRecord;
import com.erp.module.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final StockService stockService;

    /** 库存列表分页 */
    @GetMapping("/list")
    @RequirePermission("inventory:stock:list")
    public R<IPage<BasMaterial>> stockList(@Valid StockQueryDTO dto) {
        Page<BasMaterial> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<BasMaterial> data = stockService.pageStock(page, dto);
        return R.ok(data);
    }

    /** 物料库存流水 */
    @GetMapping("/record/{materialId}")
    @RequirePermission("inventory:record:list")
    public R<List<InvStockRecord>> stockRecord(@PathVariable Long materialId) {
        List<InvStockRecord> list = stockService.getRecordByMaterialId(materialId);
        return R.ok(list);
    }

    /** 库存盘点调整 */
    @PostMapping("/adjust")
    @RequirePermission("inventory:stock:adjust")
    @OperationLog(module = "库存管理", operation = "盘点调整库存")
    public R<Void> stockAdjust(@RequestBody @Valid StockAdjustDTO dto) {
        stockService.adjustStock(dto);
        return R.ok();
    }

    /** 库存预警（可用库存<安全库存） */
    @GetMapping("/warning")
    @RequirePermission("inventory:stock:warning")
    public R<List<BasMaterial>> stockWarning() {
        List<BasMaterial> warningList = stockService.getWarningMaterial();
        return R.ok(warningList);
    }

    /** 预占明细列表 */
    @GetMapping("/preoccupy")
    @RequirePermission("inventory:preoccupy:list")
    public R<List<InvPreoccupy>> preoccupyList() {
        List<InvPreoccupy> list = stockService.getAllPreoccupy();
        return R.ok(list);
    }

    // ============ 新增预占、释放接口，适配现有StockService ============
    /** 创建库存预占 */
    @PostMapping("/preoccupy/add")
    @RequirePermission("inventory:preoccupy:add")
    @OperationLog(module = "库存管理", operation = "创建库存预占")
    public R<Void> preOccupy(@RequestBody @Valid StockOccupyDTO dto) {
        stockService.preOccupy(dto);
        return R.ok();
    }

    /** 根据单据批量释放预占 */
    @PostMapping("/preoccupy/release/ref")
    @RequirePermission("inventory:preoccupy:release")
    @OperationLog(module = "库存管理", operation = "按单据释放预占")
    public R<Void> releaseByRef(@RequestParam Long refId, @RequestParam String refType) {
        stockService.releasePreoccupyByRef(refId, refType);
        return R.ok();
    }

    /** 单条预占记录释放 */
    @PostMapping("/preoccupy/release/single")
    @RequirePermission("inventory:preoccupy:release")
    @OperationLog(module = "库存管理", operation = "单条释放预占")
    public R<Void> releaseSingle(@RequestBody @Valid InvPreoccupy record) {
        stockService.releaseOccupy(record);
        return R.ok();
    }

    @PostMapping("/adjust/batch")
    @RequirePermission("inventory:stock:adjust")
    @OperationLog(module = "库存管理", operation = "批量盘点调整库存")
    public R<Void> batchAdjust(@RequestBody @javax.validation.Valid InventoryBatchAdjustDTO dto) {
        stockService.batchAdjustStock(dto);
        return R.ok();
    }
}