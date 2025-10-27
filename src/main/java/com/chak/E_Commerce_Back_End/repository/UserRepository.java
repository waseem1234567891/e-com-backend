package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    long countNewUsers(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT FUNCTION('DATE', u.lastLogin) AS date, COUNT(u) AS logins " +
            "FROM User u WHERE u.lastLogin >= :startDate GROUP BY FUNCTION('DATE', u.lastLogin) ORDER BY date")
    List<Object[]> getLoginsByDate(@Param("startDate") LocalDateTime startDate);

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
}
