package com.chak.E_Commerce_Back_End.controller;


import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;


    @GetMapping("/getAllCate")
    public List<ProductCategory> productCategories()
    {
        return productCategoryService.getCategories();
    }

    @PostMapping("/add")
    public ProductCategory addProductCategory(@RequestBody String productCategoryName)
    {
      return   productCategoryService.createCategory(productCategoryName);
    }
    @GetMapping("/getbyid/{procatId}")
    public ProductCategory getProCatId(Integer proCatId)
    {
       ProductCategory productCategory= (productCategoryService.getCateById(proCatId)).get();
       return productCategory;
    }

}
