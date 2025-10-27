package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.notifivation.NotificationDto;
import com.chak.E_Commerce_Back_End.dto.notifivation.NotificationDto1;
import com.chak.E_Commerce_Back_End.model.NotificationEntity;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.NotificationRepo;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepo notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(SimpMessagingTemplate messagingTemplate, NotificationRepo notificationRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    public void sendUserNotification(String username, Map<String, Object> payload) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        // 1️⃣ Create and save the notification
        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setMessage((String) payload.getOrDefault("message", "No message"));
        notification.setType((String) payload.getOrDefault("type", "info"));
        notification.setLink((String) payload.get("link"));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSeen(false);


        NotificationEntity save = notificationRepository.save(notification);
        //System.out.println(save.getId());
        payload.put("id",save.getId());

        // 23⃣ Try to send via WebSocket if user is online
        try {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    payload
            );
            System.out.println("📤 Sent private WS message to: " + username + " | Payload: " + payload);
        } catch (Exception e) {
            System.out.println("⚠️ User offline — notification saved in DB only");
        }


    }

    /**
     * Fetch all notifications for a user (newest first).
     */
    public List<NotificationDto1> getUserNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationDto1::fromEntity)
                .toList();
    }

    /**
     * Mark all notifications as seen for a user.
     */
    @Transactional
    public void markAllAsSeen(String username) {
        notificationRepository.markAllAsSeen(username);
    }

    /**
     * Get unseen notifications.
     */
    public List<NotificationDto1> getUnseenNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return notificationRepository.findByUserAndSeenFalse(user)
                .stream()
                .map(NotificationDto1::fromEntity)
                .toList();
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id
        );
    }
}
