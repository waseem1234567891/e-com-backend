package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.repository.ProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepo productCategoryRepo;

    public Optional<ProductCategory> getAllProductCateById(Integer id)
    {
        return productCategoryRepo.findById(id);
    }

    public List<ProductCategory> getCategories() {
        return productCategoryRepo.findAll();
    }

    public ProductCategory createCategory(String productCategoryName) {
        ProductCategory productCategory1=new ProductCategory();
        productCategory1.setProCatName(productCategoryName);
       return productCategoryRepo.save(productCategory1);
    }

    public Optional<ProductCategory> getCateById(Integer proCatId) {

        return productCategoryRepo.findById(proCatId);

    }
}
