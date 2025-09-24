package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review,Long> {


   List<Review> findByProductId(Long productId);
}
