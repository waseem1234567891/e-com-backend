package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.product.ProductCatDto;
import com.chak.E_Commerce_Back_End.exception.ProductCategoryNotFound;
import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.repository.ProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public ProductCategory updateCat(Integer procatId, ProductCatDto productCatDto) {
        Optional<ProductCategory> byId = productCategoryRepo.findById(procatId);
        if (byId.isPresent())
        {
            ProductCategory productCategory = byId.get();
            productCategory.setProCatName(productCatDto.getProCatName());
         return    productCategoryRepo.save(productCategory);
        }else {
            throw new ProductCategoryNotFound("Product category not found with id "+procatId);
        }

    }

    public ResponseEntity<?> deleteProductCategory(Integer procatId) {
        Optional<ProductCategory> byId = productCategoryRepo.findById(procatId);
        if(byId.isPresent())
        {
            ProductCategory productCategory = byId.get();
            productCategoryRepo.delete(productCategory);
            return ResponseEntity.ok("Product Category is deleted");
        }else {
            throw new ProductCategoryNotFound("Product Category not found with id "+procatId);
        }
    }
}
