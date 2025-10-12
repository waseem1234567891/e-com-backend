package com.chak.E_Commerce_Back_End.dto.product;

public class ProductStockDto {
    private String name;
    private int stock;

    public ProductStockDto(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }
}
