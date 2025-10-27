package com.chak.E_Commerce_Back_End.dto.history;

import com.chak.E_Commerce_Back_End.model.ProductStockHistory;

import java.time.LocalDateTime;

public class HistoryResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantityChanged;   // positive for addition, negative for reduction
    private int stockAfterChange;
    private String reason;
    private LocalDateTime time;

    public HistoryResponseDto() {
    }

    public HistoryResponseDto(ProductStockHistory productStockHistory)
    {
        this.id=productStockHistory.getId();
        this.productId=productStockHistory.getProduct().getId();
        this.productName=productStockHistory.getProduct().getName();
        this.quantityChanged=productStockHistory.getQuantityChanged();
        this.stockAfterChange=productStockHistory.getStockAfterChange();
        this.reason=productStockHistory.getReason();
        this.time=productStockHistory.getChangeTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(int quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStockAfterChange() {
        return stockAfterChange;
    }

    public void setStockAfterChange(int stockAfterChange) {
        this.stockAfterChange = stockAfterChange;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
