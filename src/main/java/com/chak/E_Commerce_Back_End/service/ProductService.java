package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.ProductDTO;
import com.chak.E_Commerce_Back_End.exception.ProductNotFoundException;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.ProductCategory;
import com.chak.E_Commerce_Back_End.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ProductCategoryService productCategoryService;
// Add new product
    public Product addProduct(Product product)
    {

        return productRepository.save(product);
    }
   // Get All Products Using Pagination
    public Page<Product> findAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable);
    }
// Delete a Product by product id
    public void deleteProduct(Long id) {

        productRepository.deleteById(id);
    }
    //update product
    public Product editProduct(Long id, String name, Double price, MultipartFile image, Integer categoryId, List<String> tags) throws IOException {
        Product product1=productRepository.findById(id).get();
        product1.setName(name);
        product1.setPrice(price);
        product1.setTags(tags);
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
    public Page<Product> getPaginatedProducts(int page, int size, Long categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if ((categoryId == null || categoryId == 0) && (keyword == null || keyword.isEmpty())) {
            return productRepository.findAll(pageable);
        }

        if (categoryId != null && keyword != null && !keyword.isEmpty()) {
            return productRepository.findByProductCategory_ProCatIdAndNameContainingIgnoreCase(
                    categoryId, keyword, pageable);
        }

        if (categoryId != null) {
            return productRepository.findByProductCategory_ProCatId(categoryId, pageable);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return productRepository.findAll(pageable);
    }

    // Pagination + search by keyword + category + tags
    public Page<Product> getPaginatedProduct(int page, int size, Long categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productRepository.searchProducts(categoryId, keyword, pageable);
    }


    //search product by product name

    public Product getProductByName(String productName)
    {
        Optional<Product> productOpt = productRepository.findByName(productName);
        if(productOpt.isPresent())
        {
            return productOpt.get();
        }else {
            throw new ProductNotFoundException("Product not found with Name "+productName);
        }
    }

    public Product getProductbyId(Long productId)
    {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent())
        {
            return productOpt.get();
        }else {
            throw new ProductNotFoundException("Product not Found for id "+productId);
        }
    }

}
