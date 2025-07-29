package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order,Long> {
    // Fetch orders by User's id
    List<Order> findByUser_Id(Long userId);
}
