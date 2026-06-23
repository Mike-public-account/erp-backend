package com.erp.module.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.production.dto.BomPageDTO;
import com.erp.module.production.dto.BomSaveDTO;
import com.erp.module.production.entity.ProBom;
import com.erp.module.production.vo.BomVO;
import java.util.List;

public interface ProBomService extends IService<ProBom> {
    Page<BomVO> bomPage(BomPageDTO dto);
    void saveBom(BomSaveDTO dto);
    void updateBom(Long id, BomSaveDTO dto);
    BomVO getBomInfo(Long id);
    List<BomVO> getBomByProductId(Long productId);
}