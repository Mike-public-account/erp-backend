package com.erp.module.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.DataScope;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.base.dto.CustomerPageDTO;
import com.erp.module.base.dto.CustomerSaveDTO;
import com.erp.module.base.service.BasCustomerService;
import com.erp.module.base.vo.BasCustomerVO;
import com.erp.module.system.util.LoginUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/base/customers")
@RequiredArgsConstructor
public class BasCustomerController {
    private final BasCustomerService basCustomerService;
    private final LoginUserUtil loginUserUtil;

    /** 分页列表 */
    @GetMapping("/page")
    @RequirePermission("base:customer:list")
    @DataScope(alias = "c", userIdColumn = "creator_id")
    public R<Page<BasCustomerVO>> page(@Valid CustomerPageDTO dto) {
        return R.ok(basCustomerService.pageList(dto));
    }

    /** 单条详情 */
    @GetMapping("/{id}")
    @RequirePermission("base:customer:list")
    @DataScope
    public R<BasCustomerVO> detail(@PathVariable Long id) {
        return R.ok(basCustomerService.getDetail(id));
    }

    /** 新增客户 */
    @PostMapping
    @RequirePermission("base:customer:add")
    @OperationLog(module = "基础档案", operation = "新增客户")
    public R<Long> add(@RequestBody @Valid CustomerSaveDTO dto, HttpServletRequest request) {
        Long userId = loginUserUtil.getLoginUserId(request);
        Long customerId = basCustomerService.addCustomer(dto, userId);
        return R.ok(customerId);
    }

    /** 修改客户 */
    @PutMapping("/{id}")
    @RequirePermission("base:customer:edit")
    @OperationLog(module = "基础档案", operation = "修改客户")
    public R<Void> update(@PathVariable Long id, @RequestBody @Valid CustomerSaveDTO dto, HttpServletRequest request) {
        dto.setId(id);
        Long userId = loginUserUtil.getLoginUserId(request);
        basCustomerService.updateCustomer(dto, userId);
        return R.ok();
    }

    /** 删除客户 */
    @DeleteMapping("/{id}")
    @RequirePermission("base:customer:delete")
    @OperationLog(module = "基础档案", operation = "删除客户")
    public R<Void> delete(@PathVariable Long id) {
        basCustomerService.deleteCustomer(id);
        return R.ok();
    }
}