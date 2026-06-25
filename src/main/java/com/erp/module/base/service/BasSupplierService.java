package com.erp.module.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.SupplierPageDTO;
import com.erp.module.base.dto.SupplierSaveDTO;
import com.erp.module.base.entity.BasSupplier;
import com.erp.module.base.vo.BasSupplierVO;

public interface BasSupplierService extends IService<BasSupplier> {
    Page<BasSupplierVO> pageList(SupplierPageDTO dto);
    BasSupplierVO getDetail(Long id);
    Long addSupplier(SupplierSaveDTO dto, Long loginUserId);
    void updateSupplier(SupplierSaveDTO dto, Long loginUserId);
    void deleteSupplier(Long id);
}