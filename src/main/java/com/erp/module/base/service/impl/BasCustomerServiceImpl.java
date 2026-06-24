package com.erp.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.module.base.dto.CustomerPageDTO;
import com.erp.module.base.dto.CustomerSaveDTO;
import com.erp.module.base.entity.BasCustomer;
import com.erp.module.base.mapper.BasCustomerMapper;
import com.erp.module.base.service.BasCustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BasCustomerServiceImpl extends ServiceImpl<BasCustomerMapper, BasCustomer> implements BasCustomerService {

    @Override
    public Page<BasCustomer> pageList(CustomerPageDTO dto) {
        LambdaQueryWrapper<BasCustomer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasCustomer::getCustomerCode, dto.getKeyword())
                    .or().like(BasCustomer::getCustomerName, dto.getKeyword()));
        }
        if (dto.getStatus() != null) wrapper.eq(BasCustomer::getStatus, dto.getStatus());
        wrapper.orderByDesc(BasCustomer::getCreateTime);
        return page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomer(CustomerSaveDTO dto) {
        long count = count(new LambdaQueryWrapper<BasCustomer>()
                .eq(BasCustomer::getCustomerCode, dto.getCustomerCode()));
        if (count > 0) throw new BusinessException("客户编码已存在");
        BasCustomer customer = new BasCustomer();
        BeanUtils.copyProperties(dto, customer);
        save(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(Long id, CustomerSaveDTO dto) {
        BasCustomer customer = getById(id);
        if (customer == null) throw new BusinessException("客户不存在");
        if (!customer.getCustomerCode().equals(dto.getCustomerCode())) {
            long count = count(new LambdaQueryWrapper<BasCustomer>()
                    .eq(BasCustomer::getCustomerCode, dto.getCustomerCode())
                    .ne(BasCustomer::getId, id));
            if (count > 0) throw new BusinessException("客户编码已存在");
        }
        BeanUtils.copyProperties(dto, customer);
        updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        removeById(id);
    }
}