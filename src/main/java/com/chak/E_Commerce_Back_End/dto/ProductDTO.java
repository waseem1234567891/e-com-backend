package com.chak.E_Commerce_Back_End.dto;

import com.chak.E_Commerce_Back_End.model.Product;

public class ProductDTO {

        private Long id;
        private String name;
        private Double price;
        private String imageUrl;
        private Integer proCatId;

    public Integer getProCatId() {
        return proCatId;
    }

    public void setProCatId(Integer proCatId) {
        this.proCatId = proCatId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ProductDTO(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.proCatId=product.getProductCategory().getProCatId();
            this.imageUrl = product.getImagePath();
        }
}
