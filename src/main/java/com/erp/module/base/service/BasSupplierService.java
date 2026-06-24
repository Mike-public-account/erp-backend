package com.erp.module.base.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.SupplierPageDTO;
import com.erp.module.base.dto.SupplierSaveDTO;
import com.erp.module.base.entity.BasSupplier;

public interface BasSupplierService extends IService<BasSupplier> {
    Page<BasSupplier> pageList(SupplierPageDTO dto);
    void addSupplier(SupplierSaveDTO dto);
    void updateSupplier(Long id, SupplierSaveDTO dto);
    void deleteSupplier(Long id);
}