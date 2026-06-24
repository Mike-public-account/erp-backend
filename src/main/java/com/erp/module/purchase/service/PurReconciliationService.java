package com.erp.module.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.purchase.dto.PurReconciliationDTO;
import com.erp.module.purchase.dto.PurReconciliationQueryDTO;
import com.erp.module.purchase.vo.PurReconciliationVO;

public interface PurReconciliationService {
    /** 生成供应商对账单 */
    void createReconciliation(PurReconciliationDTO dto);

    /** 分页查询对账记录 */
    Page<PurReconciliationVO> pageList(PurReconciliationQueryDTO dto);

    /** 确认对账完成 */
    void confirmReconciliation(Long reconcileId);

    /** 对账详情 */
    PurReconciliationVO getDetail(Long id);
}