package com.erp.module.sales.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.module.sales.dto.SalesOrderPageDTO;

import com.erp.module.sales.entity.SalOrder;
import com.erp.module.sales.entity.SalOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface SalOrderItemMapper extends BaseMapper<SalOrderItem> {
    /** 统计客户未结清应收总金额 */
    BigDecimal sumUnpaidAmountByCustomerId(@Param("customerId") Long customerId);

    /** 分页查询账期预警订单 */
    IPage<SalOrder> selectCreditWarningPage(IPage<SalOrder> page, @Param("dto") SalesOrderPageDTO dto);

    /** 销售单分页列表 */
    IPage<SalOrder> selectSalesPage(IPage<SalOrder> page, @Param("dto") SalesOrderPageDTO dto);
    /** 根据订单ID查询所有明细 */
    List<SalOrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /** 批量插入明细 */
    void insertBatch(@Param("list") List<SalOrderItem> itemList);
}