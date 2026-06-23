package com.erp.module.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.production.dto.WorkOrderFinishDTO;
import com.erp.module.production.dto.WorkOrderPageDTO;
import com.erp.module.production.dto.WorkOrderPickDTO;
import com.erp.module.production.dto.WorkOrderSaveDTO;
import com.erp.module.production.entity.ProWorkOrder;
import com.erp.module.production.vo.WorkOrderVO;

public interface ProWorkOrderService extends IService<ProWorkOrder> {
    Page<WorkOrderVO> workOrderPage(WorkOrderPageDTO dto);
    /** 创建工单，自动根据BOM生成原料预出库明细、锁定库存 */
    Long createWorkOrder(WorkOrderSaveDTO dto);
    WorkOrderVO getOrderDetail(Long id);
    /** 原料领料，扣减可用库存，记录实际领料数量 */
    void pickMaterial(WorkOrderPickDTO dto);
    /** 工单完工，成品入库，核算单位生产成本 */
    void finishWorkOrder(WorkOrderFinishDTO dto);
    /** 取消工单，释放全部锁定原料库存 */
    void cancelWorkOrder(Long orderId);
}