package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.ProductDTO;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ProductCategoryService productCategoryService;
// add new product
    public Product addProduct(Product product)
    {
       return productRepository.save(product);
    }

    public Page<Product> findAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    //update product
    public Product editProduct(Long id, String name, Double price, MultipartFile image, Integer categoryId) throws IOException {
        Product product1=productRepository.findById(id).get();
        product1.setName(name);
        product1.setPrice(price);
        if(image!=null&&!image.isEmpty())
        {
            String imagePath=fileStorageService.saveFile(image);
            product1.setImagePath(imagePath);
        }
        if (categoryId!=null)
        {
            ProductCategory productCategory=productCategoryService.getCateById(categoryId).orElseThrow();
            product1.setProductCategory(productCategory);

        }
        return productRepository.save(product1);
    }

    //get prodcut with pagination
    public Page<Product> getPaginatedProducts(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);

        if (categoryId == null) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findByProductCategory_ProCatId(categoryId, pageable);
        }
    }
    //search product by product name

    public Product getProductByName(String productName)
    {
        return productRepository.findByName(productName);
    }

    public Product getProductbyId(Long productId)
    {
        return productRepository.findById(productId).get();
    }

}
