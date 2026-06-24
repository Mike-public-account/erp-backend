package com.erp.module.sales.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.sales.dto.*;
import com.erp.module.sales.entity.SalOrder;
import com.erp.module.sales.vo.SalOrderVO;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface SalesOrderService extends IService<SalOrder> {
    Page<SalOrderVO> pageList(SalesOrderPageDTO dto);
    SalOrderVO getDetail(Long id);
    Long createOrder(SalesOrderSaveDTO dto);
    void cancelOrder(Long id);
    void shipGoods(SalesShipDTO dto);
    void receivePayment(SalesPaymentDTO dto);
    void exportExcel(HttpServletResponse response) throws IOException;
    void importExcel(MultipartFile file) throws Exception;


}