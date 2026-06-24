package com.erp.module.finance.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseSuggestionVO {
    // 所有成员字段统一放在类最上方
    private Long id;
    private Long materialId;
    private String materialName;
    private String materialCode;
    private BigDecimal currentStock;
    private BigDecimal safetyStock;
    private BigDecimal avgWeeklyConsumption;
    private BigDecimal suggestedQty;
    private Integer urgencyLevel;
    private String urgencyText;
    private Long supplierId;
    private String supplierName;
    private BigDecimal refPrice;
    private LocalDateTime calcTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(BigDecimal safetyStock) {
        this.safetyStock = safetyStock;
    }

    public BigDecimal getAvgWeeklyConsumption() {
        return avgWeeklyConsumption;
    }

    public void setAvgWeeklyConsumption(BigDecimal avgWeeklyConsumption) {
        this.avgWeeklyConsumption = avgWeeklyConsumption;
    }

    public BigDecimal getSuggestedQty() {
        return suggestedQty;
    }

    public void setSuggestedQty(BigDecimal suggestedQty) {
        this.suggestedQty = suggestedQty;
    }

    public Integer getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(Integer urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getUrgencyText() {
        return urgencyText;
    }

    public void setUrgencyText(String urgencyText) {
        this.urgencyText = urgencyText;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getRefPrice() {
        return refPrice;
    }

    public void setRefPrice(BigDecimal refPrice) {
        this.refPrice = refPrice;
    }

    public LocalDateTime getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(LocalDateTime calcTime) {
        this.calcTime = calcTime;
    }
}