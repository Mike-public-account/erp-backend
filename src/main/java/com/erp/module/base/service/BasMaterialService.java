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
    // 原有单条CRUD
    Page<BasMaterial> pageList(MaterialPageDTO dto);
    void addMaterial(MaterialSaveDTO dto);
    void updateMaterial(Long id, MaterialSaveDTO dto);
    void deleteMaterial(Long id);
    BasMaterialVO getStockSummary(Long id);

    // Excel导入导出（原有保留）
    void importExcel(MultipartFile file) throws Exception;
    void exportExcel(MaterialPageDTO dto, HttpServletResponse response) throws IOException;

    // 新增：批量编辑
    void batchSaveOrUpdate(MaterialBatchDTO batchDTO);

    // 新增：全物料库存汇总台账
    List<MaterialStockSummaryVO> getAllStockSummary(MaterialPageDTO dto);
}