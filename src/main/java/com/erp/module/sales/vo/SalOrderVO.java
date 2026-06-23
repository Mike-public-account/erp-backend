package com.erp.module.sales.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalOrderVO {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Long warehouseId;
    private String warehouseName;
    private Integer orderStatus;
    private String statusText;
    private Integer paymentStatus;
    private String payText;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private Long salesmanId;
    private String salesmanName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<SalOrderItemVO> itemList;
}