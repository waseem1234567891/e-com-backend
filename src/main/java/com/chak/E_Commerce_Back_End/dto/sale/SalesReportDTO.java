package com.chak.E_Commerce_Back_End.dto.sale;

import java.util.List;
import java.util.Map;

public class SalesReportDTO {
    private double totalSales;
    private long totalOrders;
    private double averageOrder;
    private List<Map<String, Object>> data;

    public SalesReportDTO() {}

    public SalesReportDTO(double totalSales, long totalOrders, double averageOrder, List<Map<String, Object>> data) {
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.averageOrder = averageOrder;
        this.data = data;
    }

    // Getters and setters
    public double getTotalSales() { return totalSales; }
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public double getAverageOrder() { return averageOrder; }
    public void setAverageOrder(double averageOrder) { this.averageOrder = averageOrder; }

    public List<Map<String, Object>> getData() { return data; }
    public void setData(List<Map<String, Object>> data) { this.data = data; }
}
