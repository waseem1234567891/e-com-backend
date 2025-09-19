package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByProductCategory_ProCatId(Long proCatId, Pageable pageable);
   Optional<Product> findByName(String proName);


}
