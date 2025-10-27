package com.chak.E_Commerce_Back_End.controller;


import com.chak.E_Commerce_Back_End.dto.product.ProductCatDto;
import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ProductCategory addProductCategory(@RequestBody ProductCatDto productCatDto)
    {
      return   productCategoryService.createCategory(productCatDto.getProCatName());
    }
    @GetMapping("/getbyid/{procatId}")
    public ProductCategory getProCatId(@PathVariable Integer proCatId)
    {
       ProductCategory productCategory= (productCategoryService.getCateById(proCatId)).get();
       return productCategory;
    }
    @PutMapping("/update/{procatId}")
    public ProductCategory updateProductCatgory(@PathVariable Integer procatId, @RequestBody ProductCatDto productCatDto)
        {
         return productCategoryService.updateCat(procatId,productCatDto);

        }

        @DeleteMapping("/delete/{procatId}")
    public ResponseEntity<?> deleteProductcatgory(@PathVariable Integer procatId)
        {
            return productCategoryService.deleteProductCategory(procatId);
        }

}