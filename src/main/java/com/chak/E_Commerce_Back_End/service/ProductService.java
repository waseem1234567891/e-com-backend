package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.ProductDTO;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FileStorageService fileStorageService;

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

    public Page<Product> getPaginatedProducts(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);

        if (categoryId == null) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findByProductCategory_ProCatId(categoryId, pageable);
        }
    }

}
