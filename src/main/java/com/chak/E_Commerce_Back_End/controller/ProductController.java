package com.chak.E_Commerce_Back_End.controller;


import com.chak.E_Commerce_Back_End.dto.ProductDTO;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.repository.ProductRepository;
import com.chak.E_Commerce_Back_End.service.FileStorageService;
import com.chak.E_Commerce_Back_End.service.ProductCategoryService;
import com.chak.E_Commerce_Back_End.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
@Autowired
private ProductService productService;
@Autowired
private FileStorageService fileStorageService;
@Autowired
private ProductCategoryService productCategoryService;

    @RequestMapping("/addproduct")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("categoryId") Integer proCatId,
            @RequestParam("image") MultipartFile image
    )
    {
        try {
            String imageUrl = fileStorageService.saveFile(image);
            Optional<ProductCategory> productCategory=productCategoryService.getAllProductCateById(proCatId);
            ProductCategory productCategory1= (ProductCategory) productCategory.get();
            Product product = new Product(null, name, price,productCategory1, imageUrl);
            productService.addProduct(product);

            return ResponseEntity.ok(new ProductDTO(product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //Get all Products using pagibation
    @RequestMapping("/allproducts")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ProductDTO>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productService.findAllProducts(pageable);
        Page<ProductDTO> productDTOPage = productPage.map(ProductDTO::new);
        return ResponseEntity.ok(productDTOPage);
    }

    //Delete a product by Product id
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteProductUsingId(@PathVariable Long productId)
    {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginated")
    public Page<Product> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Long categoryId
    ) {
        return productService.getPaginatedProducts(page, size, categoryId);
    }
//Edit product by product id
    @PutMapping("edit/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) Integer categoryId
    ) throws IOException {
        return ResponseEntity.ok(
                productService.editProduct(id, name, price, image, categoryId));
    }
   //Get product by product name
    @GetMapping("product/{proName}")
    public ResponseEntity<Product> getProductByName(@PathVariable String proName)
    {
       return ResponseEntity.ok(productService.getProductByName(proName));
    }

    //Get product by product id
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductByProductId(@PathVariable Long productId)
    {
        Product product = productService.getProductbyId(productId);
        return ResponseEntity.ok(product);
    }



    }

