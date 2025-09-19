package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<Address,Long> {

    List<Address> findByUserId(Long userId);
}
