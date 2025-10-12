package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByUser_Id(Long userId);

    Page<Order> findByStatusIn(List<OrderStatus> statuses, Pageable pageable);

    // ✅ Active statuses + search (registered & guest users)
    @Query("""
        SELECT o FROM Order o
        WHERE o.status IN :statuses
        AND (
            (o.user IS NOT NULL AND (
                LOWER(o.user.username) LIKE %:search%
                OR LOWER(o.user.firstName) LIKE %:search%
                OR LOWER(o.user.lastName) LIKE %:search%
            ))
            OR (o.user IS NULL AND (
                LOWER(o.guestName) LIKE %:search%
                OR LOWER(o.guestEmail) LIKE %:search%
            ))
            OR CAST(o.orderId AS string) LIKE %:search%
            OR LOWER(o.shippingAddress) LIKE %:search%
        )
    """)
    Page<Order> findByStatusInAndSearch(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("search") String search,
            Pageable pageable);

    // ✅ Specific status + search (registered & guest users)
    @Query("""
        SELECT o FROM Order o
        WHERE o.status = :status
        AND (
            (o.user IS NOT NULL AND (
                LOWER(o.user.username) LIKE %:search%
                OR LOWER(o.user.firstName) LIKE %:search%
                OR LOWER(o.user.lastName) LIKE %:search%
            ))
            OR (o.user IS NULL AND (
                LOWER(o.guestName) LIKE %:search%
                OR LOWER(o.guestEmail) LIKE %:search%
            ))
            OR CAST(o.orderId AS string) LIKE %:search%
            OR LOWER(o.shippingAddress) LIKE %:search%
        )
    """)
    Page<Order> findByStatusAndSearch(
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // ✅ General search (no status filter)
    @Query("""
        SELECT o FROM Order o
        WHERE
            (o.user IS NOT NULL AND (
                LOWER(o.user.username) LIKE %:search%
                OR LOWER(o.user.firstName) LIKE %:search%
                OR LOWER(o.user.lastName) LIKE %:search%
            ))
            OR (o.user IS NULL AND (
                LOWER(o.guestName) LIKE %:search%
                OR LOWER(o.guestEmail) LIKE %:search%
            ))
            OR CAST(o.orderId AS string) LIKE %:search%
            OR LOWER(o.shippingAddress) LIKE %:search%
    """)
    Page<Order> searchOrders(@Param("search") String search, Pageable pageable);


    // Daily sales
    @Query(value = "SELECT DATE(order_date) AS date, SUM(total_amount) AS revenue " +
            "FROM orders " +
            "WHERE status != 'CANCELLED' " +
            "GROUP BY DATE(order_date) " +
            "ORDER BY DATE(order_date)",
            nativeQuery = true)
    List<Object[]> getDailySalesNative();

    // Weekly sales (label = YYYY-WW)
    @Query(value = "SELECT DATE_FORMAT(order_date, '%x-%v') AS week, SUM(total_amount) AS revenue " +
            "FROM orders " +
            "WHERE status != 'CANCELLED' " +
            "GROUP BY DATE_FORMAT(order_date, '%x-%v') " +
            "ORDER BY DATE_FORMAT(order_date, '%x-%v')",
            nativeQuery = true)
    List<Object[]> getWeeklySalesNative();

    // Monthly sales (label = YYYY-MM)
    @Query(value = "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, SUM(total_amount) AS revenue " +
            "FROM orders " +
            "WHERE status != 'CANCELLED' " +
            "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
            "ORDER BY DATE_FORMAT(order_date, '%Y-%m')",
            nativeQuery = true)
    List<Object[]> getMonthlySalesNative();

    // Summary: total sales and total orders
    @Query(value = "SELECT COALESCE(SUM(total_amount),0), COUNT(*) FROM orders WHERE status != 'CANCELLED'", nativeQuery = true)
    Object[] getSalesSummaryNative();






    public interface SalesSummary {
        Double getTotalSales();
        Long getTotalOrders();
    }

    @Query(value = "SELECT COALESCE(SUM(total_amount),0) AS totalSales, COUNT(*) AS totalOrders " +
            "FROM orders WHERE status != 'CANCELLED'", nativeQuery = true)
    SalesSummary getSalesSummaryProjection();


    // Count total orders in a period (optional)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate AND o.status != com.chak.E_Commerce_Back_End.model.enums.OrderStatus.CANCELLED")
    long countOrdersFrom(@Param("startDate") LocalDateTime startDate);

    // ✅ Returns daily order counts (ignores CANCELLED)
    @QueryHints(@QueryHint(name = org.hibernate.jpa.HibernateHints.HINT_CACHEABLE, value = "false"))
    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, COUNT(o) AS orders " +
            "FROM Order o " +
            "WHERE o.orderDate >= :startDate AND o.status <> com.chak.E_Commerce_Back_End.model.enums.OrderStatus.CANCELLED " +
            "GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY date")
    List<Object[]> getOrdersByDate(@Param("startDate") LocalDateTime startDate);

    // Optional: orders per week/month if needed
    @Query("SELECT FUNCTION('YEAR', o.orderDate) AS yr, FUNCTION('WEEK', o.orderDate) AS wk, COUNT(o) " +
            "FROM Order o " +
            "WHERE o.orderDate >= :startDate AND o.status != com.chak.E_Commerce_Back_End.model.enums.OrderStatus.CANCELLED " +
            "GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('WEEK', o.orderDate) " +
            "ORDER BY yr, wk")
    List<Object[]> getOrdersByWeek(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m') AS month, COUNT(o) " +
            "FROM Order o " +
            "WHERE o.orderDate >= :startDate AND o.status != com.chak.E_Commerce_Back_End.model.enums.OrderStatus.CANCELLED " +
            "GROUP BY month " +
            "ORDER BY month")
    List<Object[]> getOrdersByMonth(@Param("startDate") LocalDateTime startDate);

}

