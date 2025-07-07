package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order,Long> {
}
