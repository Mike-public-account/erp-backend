package com.erp.module.purchase.service;
import com.erp.module.purchase.dto.PurPrepayDTO;
import com.erp.module.purchase.dto.PurPrepayQueryDTO;
import com.erp.module.purchase.vo.PurPrepayVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;

public interface PurPrepayService {
    /** 创建采购预付款 */
    void createPrepay(PurPrepayDTO dto);
    /** 预付款核销（入库抵扣） */
    void writeOffPrepay(Long prepayId, Long orderItemId, BigDecimal writeOffAmount);
    /** 分页查询预付款 */
    Page<PurPrepayVO> page(PurPrepayQueryDTO dto);
    /** 查询预付款详情 */
    PurPrepayVO getDetail(Long id);
}