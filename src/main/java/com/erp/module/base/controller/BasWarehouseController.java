package com.erp.module.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.DataScope;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.base.dto.WarehousePageDTO;
import com.erp.module.base.dto.WarehouseSaveDTO;
import com.erp.module.base.service.BasWarehouseService;
import com.erp.module.base.vo.BasWarehouseVO;
import com.erp.module.system.util.LoginUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/base/warehouses")
@RequiredArgsConstructor
public class BasWarehouseController {
    private final BasWarehouseService basWarehouseService;
    private final LoginUserUtil loginUserUtil;

    @GetMapping("/page")
    @RequirePermission("base:warehouse:list")
    @DataScope(alias = "w", userIdColumn = "creator_id")
    public R<Page<BasWarehouseVO>> page(@Valid WarehousePageDTO dto) {
        return R.ok(basWarehouseService.pageList(dto));
    }

    @GetMapping("/{id}")
    @RequirePermission("base:warehouse:list")
    @DataScope
    public R<BasWarehouseVO> detail(@PathVariable Long id) {
        return R.ok(basWarehouseService.getDetail(id));
    }

    @PostMapping
    @RequirePermission("base:warehouse:add")
    @OperationLog(module = "基础档案", operation = "新增仓库")
    public R<Long> add(@RequestBody @Valid WarehouseSaveDTO dto, HttpServletRequest request) {
        Long userId = loginUserUtil.getLoginUserId(request);
        Long warehouseId = basWarehouseService.addWarehouse(dto, userId);
        return R.ok(warehouseId);
    }

    @PutMapping("/{id}")
    @RequirePermission("base:warehouse:edit")
    @OperationLog(module = "基础档案", operation = "修改仓库")
    public R<Void> update(@PathVariable Long id, @RequestBody @Valid WarehouseSaveDTO dto, HttpServletRequest request) {
        dto.setId(id);
        Long userId = loginUserUtil.getLoginUserId(request);
        basWarehouseService.updateWarehouse( dto,userId);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("base:warehouse:delete")
    @OperationLog(module = "基础档案", operation = "删除仓库")
    public R<Void> delete(@PathVariable Long id) {
        basWarehouseService.deleteWarehouse(id);
        return R.ok();
    }
}