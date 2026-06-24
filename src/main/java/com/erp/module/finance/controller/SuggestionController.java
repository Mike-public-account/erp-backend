package com.erp.module.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.result.R;
import com.erp.module.finance.dto.SuggestionQueryDTO;
import com.erp.module.finance.service.SuggestionService;
import com.erp.module.finance.vo.PurchaseSuggestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/finance/suggestion")
@RequiredArgsConstructor
public class SuggestionController {
    private final SuggestionService suggestionService;

    @GetMapping("/list")
    public R<Page<PurchaseSuggestionVO>> page(@Valid SuggestionQueryDTO dto) {
        return R.ok(suggestionService.pageSuggest(dto));
    }

    @PostMapping("/refresh")
    public R<Void> refresh() {
        suggestionService.refreshAll();
        return R.ok();
    }
}