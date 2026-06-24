package com.erp.module.purchase.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.purchase.entity.PurOrderAudit;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PurOrderAuditMapper extends BaseMapper<PurOrderAudit> {
    // 根据采购单查询审批记录
    List<PurOrderAudit> selectByOrderId(Long orderId);
}