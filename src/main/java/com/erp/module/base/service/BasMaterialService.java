package com.erp.module.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.base.dto.MaterialBatchDTO;
import com.erp.module.base.dto.MaterialPageDTO;
import com.erp.module.base.dto.MaterialSaveDTO;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.vo.BasMaterialVO;
import com.erp.module.base.vo.MaterialStockSummaryVO;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface BasMaterialService extends IService<BasMaterial> {
    // 分页返回VO
    Page<BasMaterialVO> pageList(MaterialPageDTO dto);
    // 新增统一详情方法
    BasMaterialVO getDetail(Long id);
    Long addMaterial(MaterialSaveDTO dto);
    void updateMaterial(Long id, MaterialSaveDTO dto);
    void deleteMaterial(Long id);
    MaterialStockSummaryVO getStockSummary(Long id);
    List<MaterialStockSummaryVO> getAllStockSummary(MaterialPageDTO dto);
    void batchSaveOrUpdate(MaterialBatchDTO batchDTO);
    void importExcel(MultipartFile file) throws Exception;
    void exportExcel(MaterialPageDTO dto, HttpServletResponse response) throws IOException;
}