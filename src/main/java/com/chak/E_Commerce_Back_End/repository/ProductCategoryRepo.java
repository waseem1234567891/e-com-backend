package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepo extends JpaRepository<ProductCategory,Integer> {
}
