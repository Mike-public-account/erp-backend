package com.erp.module.base.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.CustomerPageDTO;
import com.erp.module.base.dto.CustomerSaveDTO;
import com.erp.module.base.entity.BasCustomer;

public interface BasCustomerService extends IService<BasCustomer> {
    Page<BasCustomer> pageList(CustomerPageDTO dto);
    void addCustomer(CustomerSaveDTO dto);
    void updateCustomer(Long id, CustomerSaveDTO dto);
    void deleteCustomer(Long id);
}