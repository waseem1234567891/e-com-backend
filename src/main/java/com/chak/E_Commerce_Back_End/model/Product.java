package com.chak.E_Commerce_Back_End.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String name;
    private double price;
    private String imagePath;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "product_category_pro_cat_id", nullable = false)
    private ProductCategory productCategory;


    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Product() {
    }

    public Product(Long id, String name, double price,ProductCategory productCategory, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.productCategory=productCategory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }
}
