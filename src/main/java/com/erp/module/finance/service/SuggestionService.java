package com.erp.module.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.finance.dto.SuggestionQueryDTO;
import com.erp.module.finance.entity.FinPurchaseSuggestion;
import com.erp.module.finance.vo.PurchaseSuggestionVO;

public interface SuggestionService extends IService<FinPurchaseSuggestion> {
    Page<PurchaseSuggestionVO> pageSuggest(SuggestionQueryDTO dto);
    // 刷新全部备货建议
    void refreshAll();
}