package com.chak.E_Commerce_Back_End.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer proCatId;
    private String proCatName;

    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    public void setProCatId(Integer proCatId) {
        this.proCatId = proCatId;
    }

    public void setProCatName(String proCatName) {
        this.proCatName = proCatName;
    }

    public Integer getProCatId() {
        return proCatId;
    }

    public String getProCatName() {
        return proCatName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
