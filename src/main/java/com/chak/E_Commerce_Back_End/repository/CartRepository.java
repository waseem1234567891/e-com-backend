package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.CartItem;
import com.chak.E_Commerce_Back_End.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByUser(User user);
    void deleteByUser(User user);
    void deleteByUserAndProduct_Id(User user, Long productId);
}
