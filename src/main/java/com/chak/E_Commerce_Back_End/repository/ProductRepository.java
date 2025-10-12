package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.dto.product.ProductStockDto;
import com.chak.E_Commerce_Back_End.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByProductCategory_ProCatId(Long proCatId, Pageable pageable);
   Optional<Product> findByName(String proName);


    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByProductCategory_ProCatIdAndNameContainingIgnoreCase(Long categoryId, String keyword, Pageable pageable);

    // Search by keyword in name OR tags, optionally filter by category
    @Query("SELECT p FROM Product p " +
            "WHERE (:categoryId IS NULL OR p.productCategory.proCatId = :categoryId) " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR :keyword MEMBER OF p.tags)")
    Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    @Query("SELECT p.stock FROM Product p WHERE p.id = :productId")
    Integer findStockByProductId(@Param("productId") Long productId);

    @Query("SELECT new com.chak.E_Commerce_Back_End.dto.product.ProductStockDto(p.name, p.stock) FROM Product p")
    List<ProductStockDto> findAllProductNameAndStock();

    // ✅ Fetch products with stock less than threshold
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold ORDER BY p.stock ASC")
    List<Product> findLowStockProducts(int threshold);
}
