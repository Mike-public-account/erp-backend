package com.erp.module.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.purchase.dto.PurOrderImportDTO;
import com.erp.module.purchase.dto.PurchaseOrderDTO;
import com.erp.module.purchase.dto.PurchasePageDTO;
import com.erp.module.purchase.entity.PurOrder;
import com.erp.module.purchase.vo.PurchaseOrderVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface PurchaseOrderService extends IService<PurOrder> {
    Page<PurchaseOrderVO> pageList(PurchasePageDTO dto);
    Long createOrder(PurchaseOrderDTO dto);
    PurchaseOrderVO getDetail(Long id);
    void submitAudit(Long id);
    void auditOrder(Long id, Integer pass, String remark);
    void receiptStock(Long id);
    void cancelOrder(Long id);
    Page<PurchaseOrderVO> getTimeoutOrder(PurchasePageDTO dto);
    void importExcel(MultipartFile file) throws Exception;
    void exportExcel(HttpServletResponse response) throws IOException;
    /** Excel批量导入采购单 */
    List<PurOrder> importOrder(List<PurOrderImportDTO> importList, Long loginUserId);
}