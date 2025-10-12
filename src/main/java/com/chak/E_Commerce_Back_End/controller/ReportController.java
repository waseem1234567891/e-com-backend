package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.sale.SalesReportDTO;
import com.chak.E_Commerce_Back_End.service.ReportService;
import com.chak.E_Commerce_Back_End.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {
@Autowired
    private  ReportService reportService;




    @GetMapping("/sales")
    public SalesReportDTO getSalesReport(@RequestParam(defaultValue = "daily") String period) {
        return reportService.getSalesReport(period);
    }

    @GetMapping("/user-activity")
    public ResponseEntity<?> getUserActivityOverview(@RequestParam(defaultValue = "daily") String period) {
        try {
            return ResponseEntity.ok(reportService.getUserActivityOverview(period));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    "Error fetching user activity overview: " + e.getMessage()
            );
        }
    }

    /**
     * ✅ INVENTORY REPORT (Low Stock)
     * Example: GET /reports/inventory?threshold=10
     */
    @GetMapping("/inventory")
    public ResponseEntity<List<Map<String, Object>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        List<Map<String, Object>> result = reportService.getLowStockProducts(threshold);
        return ResponseEntity.ok(result);
    }
}
