package com.chak.E_Commerce_Back_End.service;


import com.chak.E_Commerce_Back_End.dto.sale.SalesReportDTO;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.repository.OrderRepo.SalesSummary;
import com.chak.E_Commerce_Back_End.repository.ProductRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReportService {

    private final OrderRepo orderRepo;
    private final UserRepository userRepository;
    private  final ProductRepository productRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public ReportService(OrderRepo orderRepo, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepo = orderRepo;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Generates a sales report for the specified period: daily, weekly, or monthly.
     *
     * @param period "daily", "weekly", or "monthly"
     * @return SalesReportDTO containing totalSales, totalOrders, averageOrder, and data list
     */
    public SalesReportDTO getSalesReport(String period) {

        List<Object[]> rawData;

        switch (period.toLowerCase()) {
            case "weekly":
                rawData = orderRepo.getWeeklySalesNative();
                break;
            case "monthly":
                rawData = orderRepo.getMonthlySalesNative();
                break;
            default:
                rawData = orderRepo.getDailySalesNative();
        }

        // Convert raw data to frontend-friendly format
        List<Map<String, Object>> formattedData = new ArrayList<>();
        for (Object[] row : rawData) {
            String label = row[0].toString();
            double revenue = 0.0;

            if (row[1] instanceof Number) {
                revenue = ((Number) row[1]).doubleValue();
            } else if (row[1] instanceof java.math.BigDecimal) {
                revenue = ((java.math.BigDecimal) row[1]).doubleValue();
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("date", label);
            entry.put("revenue", revenue);
            formattedData.add(entry);
        }

        // Use Spring Projection to safely fetch summary
        SalesSummary summary = orderRepo.getSalesSummaryProjection();
        double totalSales = summary.getTotalSales() != null ? summary.getTotalSales() : 0;
        long totalOrders = summary.getTotalOrders() != null ? summary.getTotalOrders() : 0;
        double averageOrder = totalOrders > 0 ? totalSales / totalOrders : 0;

        return new SalesReportDTO(totalSales, totalOrders, averageOrder, formattedData);
    }

    //Get user activity
    @Transactional(readOnly = true)
    public Map<String, Object> getUserActivityOverview(String period) {
        Map<String, Object> result = new HashMap<>();

        // Determine start date
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        switch (period.toLowerCase()) {
            case "weekly": startDate = now.minus(7, ChronoUnit.DAYS); break;
            case "monthly": startDate = now.minus(1, ChronoUnit.MONTHS); break;
            default: startDate = now.minus(1, ChronoUnit.DAYS); break;
        }

        long totalUsers = userRepository.countAllUsers();
        long activeUsers = userRepository.countActiveUsers();
        long newUsers = userRepository.countNewUsers(startDate);
        long totalOrders = orderRepo.countOrdersFrom(startDate); // optionally filter by startDate
        double avgOrdersPerUser = activeUsers > 0 ? (double) totalOrders / activeUsers : 0;

        result.put("totalUsers", totalUsers);
        result.put("activeUsers", activeUsers);
        result.put("newUsers", newUsers);
        result.put("totalOrders", totalOrders);
        result.put("averageOrdersPerUser", avgOrdersPerUser);

        // Chart data
        List<Object[]> loginsData = userRepository.getLoginsByDate(startDate);
        List<Object[]> ordersData = orderRepo.getOrdersByDate(startDate);

        Map<String, Map<String, Long>> combined = new TreeMap<>();
        for (Object[] obj : loginsData) {
            String date = obj[0].toString();
            Long logins = ((Number) obj[1]).longValue();
            combined.putIfAbsent(date, new HashMap<>());
            combined.get(date).put("logins", logins);
        }
        for (Object[] obj : ordersData) {
            String date = obj[0].toString();
            Long orders = ((Number) obj[1]).longValue();
            combined.putIfAbsent(date, new HashMap<>());
            combined.get(date).put("orders", orders);
        }

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (String date : combined.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", date);
            map.put("logins", combined.get(date).getOrDefault("logins", 0L));
            map.put("orders", combined.get(date).getOrDefault("orders", 0L));
            chartData.add(map);
        }

        result.put("data", chartData);

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLowStockProducts(int threshold) {
        List<Product> products = productRepository.findLowStockProducts(threshold);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("category", p.getProductCategory() != null ? p.getProductCategory().getProCatName() : "Uncategorized");
            map.put("stockQuantity", p.getStock());
            map.put("status", p.getStock() == 0 ? "Out of Stock" : "Low Stock");
            result.add(map);
        }

        return result;
    }

}
