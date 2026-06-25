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
import com.erp.module.base.vo.BasCustomerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasCustomerServiceImpl extends ServiceImpl<BasCustomerMapper, BasCustomer> implements BasCustomerService {

    @Override
    public Page<BasCustomerVO> pageList(CustomerPageDTO dto) {
        LambdaQueryWrapper<BasCustomer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(BasCustomer::getCustomerCode, dto.getKeyword())
                    .or().like(BasCustomer::getCustomerName, dto.getKeyword()));
        }
        if (dto.getStatus() != null) wrapper.eq(BasCustomer::getStatus, dto.getStatus());
        wrapper.orderByDesc(BasCustomer::getCreateTime);
        Page<BasCustomer> entityPage = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        // 实体转VO
        List<BasCustomerVO> voList = entityPage.getRecords().stream().map(entity -> {
            BasCustomerVO vo = new BasCustomerVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<BasCustomerVO> voPage = new Page<>();
        voPage.setTotal(entityPage.getTotal());
        voPage.setRecords(voList);
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        return voPage;
    }

    @Override
    public BasCustomerVO getDetail(Long id) {
        BasCustomer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户数据不存在");
        }
        BasCustomerVO vo = new BasCustomerVO();
        BeanUtils.copyProperties(customer, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCustomer(CustomerSaveDTO dto, Long loginUserId) {
        long count = count(new LambdaQueryWrapper<BasCustomer>()
                .eq(BasCustomer::getCustomerCode, dto.getCustomerCode()));
        if (count > 0) throw new BusinessException("客户编码已存在");
        BasCustomer customer = new BasCustomer();
        BeanUtils.copyProperties(dto, customer);
        // 如需创建人可赋值 customer.setCreatorId(loginUserId);
        save(customer);
        return customer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(CustomerSaveDTO dto, Long loginUserId) {
        Long id = dto.getId();
        BasCustomer customer = getById(id);
        if (customer == null) throw new BusinessException("客户不存在");
        // 编码唯一校验（排除自身）
        if (!customer.getCustomerCode().equals(dto.getCustomerCode())) {
            long count = count(new LambdaQueryWrapper<BasCustomer>()
                    .eq(BasCustomer::getCustomerCode, dto.getCustomerCode())
                    .ne(BasCustomer::getId, id));
            if (count > 0) throw new BusinessException("客户编码已存在");
        }
        BeanUtils.copyProperties(dto, customer);
        // 如需修改人可赋值 customer.setUpdaterId(loginUserId);
        updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        removeById(id);
    }
}