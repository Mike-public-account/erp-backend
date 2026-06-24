package com.erp.module.base.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.WarehousePageDTO;
import com.erp.module.base.dto.WarehouseSaveDTO;
import com.erp.module.base.entity.BasWarehouse;

public interface BasWarehouseService extends IService<BasWarehouse> {
    Page<BasWarehouse> pageList(WarehousePageDTO dto);
    void addWarehouse(WarehouseSaveDTO dto);
    void updateWarehouse(Long id, WarehouseSaveDTO dto);
    void deleteWarehouse(Long id);
}