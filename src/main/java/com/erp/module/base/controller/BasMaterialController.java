package com.erp.module.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.DataScope;
import com.erp.common.annotation.OperationLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.R;
import com.erp.module.base.dto.MaterialBatchDTO;
import com.erp.module.base.dto.MaterialPageDTO;
import com.erp.module.base.dto.MaterialSaveDTO;
import com.erp.module.base.service.BasMaterialService;
import com.erp.module.base.vo.BasMaterialVO;
import com.erp.module.base.vo.MaterialStockSummaryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api(tags = "物料管理")
@RestController
@RequestMapping("/api/v1/base/materials") // 修复：material → materials 匹配文档
@RequiredArgsConstructor
public class BasMaterialController {

    private final BasMaterialService materialService;

    /**
     * 分页查询物料列表（统一/page规范，返回VO）
     */
    @GetMapping("/page")
    @ApiOperation("分页查询物料")
    @RequirePermission("base:material:list")
    @DataScope(alias = "m", userIdColumn = "creator_id")
    public R<Page<BasMaterialVO>> page(@Valid MaterialPageDTO dto) {
        return R.ok(materialService.pageList(dto));
    }

    /**
     * 根据ID查询物料详情（统一/{id}标准接口）
     */
    @GetMapping("/{id}")
    @ApiOperation("物料单条详情")
    @RequirePermission("base:material:list")
    @DataScope
    public R<BasMaterialVO> detail(@PathVariable Long id) {
        return R.ok(materialService.getDetail(id));
    }

    /**
     * 新增物料
     */
    @PostMapping
    @ApiOperation("新增物料")
    @RequirePermission("base:material:add")
    @OperationLog(module = "基础档案", operation = "新增物料")
    public R<Long> add(@RequestBody @Valid MaterialSaveDTO dto) {
        Long id = materialService.addMaterial(dto);
        return R.ok(id);
    }

    /**
     * 修改物料
     */
    @PutMapping("/{id}")
    @ApiOperation("修改物料")
    @RequirePermission("base:material:edit")
    @OperationLog(module = "基础档案", operation = "修改物料")
    public R<Void> update(@PathVariable Long id, @RequestBody @Valid MaterialSaveDTO dto) {
        materialService.updateMaterial(id, dto);
        return R.ok();
    }

    /**
     * 删除物料（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除物料")
    @RequirePermission("base:material:delete")
    @OperationLog(module = "基础档案", operation = "删除物料")
    public R<Void> delete(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return R.ok();
    }

    /**
     * 单物料库存汇总详情（修复路径匹配文档 /{id}/stock-summary）
     */
    @GetMapping("/{id}/stock-summary")
    @ApiOperation("查询单个物料库存详情")
    @RequirePermission("base:material:list")
    @DataScope
    public R<MaterialStockSummaryVO> getStockSummary(@PathVariable Long id) {
        return R.ok(materialService.getStockSummary(id));
    }

    /**
     * 全量物料库存汇总列表
     */
    @GetMapping("/stock/all")
    @ApiOperation("全物料库存汇总查询")
    @RequirePermission("base:material:stock")
    public R<List<MaterialStockSummaryVO>> getAllStockSummary(@Valid MaterialPageDTO dto) {
        return R.ok(materialService.getAllStockSummary(dto));
    }

    /**
     * 批量新增/修改物料
     */
    @PostMapping("/batch")
    @ApiOperation("批量维护物料（新增/更新）")
    @RequirePermission("base:material:batch")
    @OperationLog(module = "基础档案", operation = "批量维护物料")
    public R<Void> batchSave(@RequestBody @Valid MaterialBatchDTO batchDTO) {
        materialService.batchSaveOrUpdate(batchDTO);
        return R.ok();
    }

    /**
     * Excel导入物料
     */
    @PostMapping("/import")
    @ApiOperation("Excel批量导入物料")
    @RequirePermission("base:material:import")
    @OperationLog(module = "基础档案", operation = "物料Excel导入")
    public R<Void> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        materialService.importExcel(file);
        return R.ok();
    }

    /**
     * Excel导出物料
     */
    @GetMapping("/export")
    @ApiOperation("Excel导出物料列表")
    @RequirePermission("base:material:export")
    @OperationLog(module = "基础档案", operation = "物料导出")
    public void export(@Valid MaterialPageDTO dto, HttpServletResponse response) throws IOException {
        materialService.exportExcel(dto, response);
    }
}