package com.chak.E_Commerce_Back_End.dto.product;

import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.ProductCategory;

public class ProductRequestDto {

    private String name;
    private double price;
    private String imagePath;
    private int stock;
    private ProductCategory productCategory;

    public ProductRequestDto() {
    }

    public ProductRequestDto(String name, double price, String imagePath, int stock, ProductCategory productCategory) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.stock = stock;
        this.productCategory = productCategory;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
}
