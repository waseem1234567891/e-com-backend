package com.chak.E_Commerce_Back_End.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ProductStockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private int quantityChanged;   // positive for addition, negative for reduction
    private int stockAfterChange;  // stock level after this change

    private String reason;         // e.g., "Order Placed", "Stock Refill", "Order Cancelled"

    private LocalDateTime changeTime = LocalDateTime.now(); // timestamp

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(int quantityChanged) {
        this.quantityChanged = quantityChanged;
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

    public LocalDateTime getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }
}

