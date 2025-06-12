package com.chak.E_Commerce_Back_End.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProductCategory {
    @Id
    private Integer proCatId;
    private String proCatName;

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
}
