package com.chak.E_Commerce_Back_End.repository;

import com.chak.E_Commerce_Back_End.model.NotificationEntity;
import com.chak.E_Commerce_Back_End.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepo extends JpaRepository<NotificationEntity,Long> {

    List<NotificationEntity> findByUserOrderByCreatedAtDesc(User user);
    List<NotificationEntity> findByUserAndSeenFalse(User user);

    // 🔹 Custom update query to mark all as seen
    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.seen = true WHERE n.user.username = :username AND n.seen = false")
    void markAllAsSeen(String username);

}
