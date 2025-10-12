package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.ProductStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStockHistoryRepo extends JpaRepository<ProductStockHistory,Long> {
    List<ProductStockHistory> findByProductId(Long productId);
}
