package com.erp.module.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.CustomerPageDTO;
import com.erp.module.base.dto.CustomerSaveDTO;
import com.erp.module.base.entity.BasCustomer;
import com.erp.module.base.vo.BasCustomerVO;

public interface BasCustomerService extends IService<BasCustomer> {
    // 分页列表
    Page<BasCustomerVO> pageList(CustomerPageDTO dto);
    // 详情
    BasCustomerVO getDetail(Long id);
    // 新增/编辑
    Long addCustomer(CustomerSaveDTO dto, Long loginUserId);
    void updateCustomer(CustomerSaveDTO dto, Long loginUserId);
    // 删除
    void deleteCustomer(Long id);
}