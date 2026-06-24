package com.erp.module.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.base.entity.BasCustomer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户表 Mapper 接口
 */
@Mapper
public interface BasCustomerMapper extends BaseMapper<BasCustomer> {

}