package com.erp.module.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.DataScope;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.base.dto.SupplierPageDTO;
import com.erp.module.base.dto.SupplierSaveDTO;
import com.erp.module.base.service.BasSupplierService;
import com.erp.module.base.vo.BasSupplierVO;
import com.erp.module.system.util.LoginUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/base/suppliers")
@RequiredArgsConstructor
public class BasSupplierController {
    private final BasSupplierService basSupplierService;
    private final LoginUserUtil loginUserUtil;

    /** 分页列表 */
    @GetMapping("/page")
    @RequirePermission("base:supplier:list")
    @DataScope(alias = "s", userIdColumn = "creator_id")
    public R<Page<BasSupplierVO>> page(@Valid SupplierPageDTO dto) {
        return R.ok(basSupplierService.pageList(dto));
    }

    /** 单条详情 */
    @GetMapping("/{id}")
    @RequirePermission("base:supplier:list")
    @DataScope
    public R<BasSupplierVO> detail(@PathVariable Long id) {
        return R.ok(basSupplierService.getDetail(id));
    }

    /** 新增供应商 */
    @PostMapping
    @RequirePermission("base:supplier:add")
    @OperationLog(module = "基础档案", operation = "新增供应商")
    public R<Long> add(@RequestBody @Valid SupplierSaveDTO dto, HttpServletRequest request) {
        Long userId = loginUserUtil.getLoginUserId(request);
        Long supplierId = basSupplierService.addSupplier(dto, userId);
        return R.ok(supplierId);
    }

    /** 修改供应商 */
    @PutMapping("/{id}")
    @RequirePermission("base:supplier:edit")
    @OperationLog(module = "基础档案", operation = "修改供应商")
    public R<Void> update(@PathVariable Long id, @RequestBody @Valid SupplierSaveDTO dto, HttpServletRequest request) {
        dto.setId(id);
        Long userId = loginUserUtil.getLoginUserId(request);
        basSupplierService.updateSupplier(dto, userId);
        return R.ok();
    }

    /** 删除供应商 */
    @DeleteMapping("/{id}")
    @RequirePermission("base:supplier:delete")
    @OperationLog(module = "基础档案", operation = "删除供应商")
    public R<Void> delete(@PathVariable Long id) {
        basSupplierService.deleteSupplier(id);
        return R.ok();
    }
}