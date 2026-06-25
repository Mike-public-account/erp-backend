package com.erp.module.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.WarehousePageDTO;
import com.erp.module.base.dto.WarehouseSaveDTO;
import com.erp.module.base.entity.BasWarehouse;
import com.erp.module.base.vo.BasWarehouseVO;

public interface BasWarehouseService extends IService<BasWarehouse> {
    // 修复返回值：Page<BasWarehouseVO>
    Page<BasWarehouseVO> pageList(WarehousePageDTO dto);
    BasWarehouseVO getDetail(Long id);
    Long addWarehouse(WarehouseSaveDTO dto, Long loginUserId);
    void updateWarehouse(WarehouseSaveDTO dto, Long loginUserId);
    void deleteWarehouse(Long id);
}