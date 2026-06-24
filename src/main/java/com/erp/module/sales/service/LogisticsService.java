package com.erp.module.sales.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.sales.dto.LogisticsReconcileDTO;
import com.erp.module.sales.entity.SalLogistics;
import com.erp.module.sales.vo.LogisticsVO;
import java.util.List;

public interface LogisticsService extends IService<SalLogistics> {
    List<LogisticsVO> getByOrderId(Long orderId);
    void reconcile(LogisticsReconcileDTO dto);
}