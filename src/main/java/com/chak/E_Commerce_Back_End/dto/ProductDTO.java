package com.chak.E_Commerce_Back_End.dto;

import com.chak.E_Commerce_Back_End.model.Product;

import java.util.List;

public class ProductDTO {

        private Long id;
        private String name;
        private Double price;
        private String imageUrl;
        private Integer proCatId;
        private List<String> tags;

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ProductDTO(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.proCatId=product.getProductCategory().getProCatId();
            this.imageUrl = product.getImagePath();
            this.tags=product.getTags();
        }
}
