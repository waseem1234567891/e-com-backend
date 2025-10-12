package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.history.HistoryResponseDto;
import com.chak.E_Commerce_Back_End.model.ProductStockHistory;
import com.chak.E_Commerce_Back_End.repository.ProductStockHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductStockHistoryService {

    @Autowired
    private ProductStockHistoryRepo productStockHistoryRepo;

    public void save(ProductStockHistory history) {
        productStockHistoryRepo.save(history);
    }

    public List<ProductStockHistory> getAllTransactions() {
        return productStockHistoryRepo.findAll();
    }

    public List<HistoryResponseDto> getTransactionsByProduct(Long productId) {
        List<ProductStockHistory> productHistory = productStockHistoryRepo.findByProductId(productId);
        List<HistoryResponseDto> collect = productHistory.stream().map(HistoryResponseDto::new).collect(Collectors.toList());
        return collect;
    }
}
