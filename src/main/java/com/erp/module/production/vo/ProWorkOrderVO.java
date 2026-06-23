package com.erp.module.production.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProWorkOrderVO {
    private Long id;
    private String workOrderNo;
    private Long productId;
    private String productName;
    private BigDecimal planQty;
    private BigDecimal actualQty;
    private Integer orderStatus;
    private String statusText;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private LocalDateTime actualEndTime;
    private Long warehouseId;
    private String warehouseName;
    private Long creatorId;
    private String creatorName;
    private BigDecimal totalMaterialCost;
    private BigDecimal unitMaterialCost;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<ProWorkOrderMaterialVO> materialList;
}