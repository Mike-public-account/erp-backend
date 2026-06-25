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

    // 修复1：新增loginUserId入参，匹配Controller传参
    Long createOrder(SalesOrderSaveDTO dto, Long loginUserId);

    void cancelOrder(Long id);

    // 修复2：重载/新增适配Controller的发货方法，兼容id+warehouseId调用
    void shipGoods(Long orderId, Long warehouseId);
    // 保留原有DTO重载，供批量/前端完整DTO提交使用
    void shipGoods(SalesShipDTO dto);

    void receivePayment(SalesPaymentDTO dto);

    // 修复3：导出增加分页查询DTO，返回VO列表给Controller统一导出
    List<SalOrderVO> exportExcel(SalesOrderPageDTO dto, HttpServletResponse response) throws IOException;

    void importExcel(MultipartFile file) throws Exception;

    // 新增缺失方法1：账期预警分页
    Page<SalOrderVO> listCreditWarning(SalesOrderPageDTO dto);

    // 新增缺失方法2：单订单毛利计算
    BigDecimal calcSingleOrderGross(Long orderId);
}