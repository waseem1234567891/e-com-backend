package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.history.HistoryResponseDto;
import com.chak.E_Commerce_Back_End.model.ProductStockHistory;
import com.chak.E_Commerce_Back_End.service.ProductStockHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
public class ProductStockHistoryController {
    @Autowired
    private ProductStockHistoryService productStockHistoryService;

    @GetMapping("/product/{productId}")
    public List<HistoryResponseDto> getHistoryByProduct(@PathVariable Long productId)
    {
        return productStockHistoryService.getTransactionsByProduct(productId);
    }

}
