package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.notifivation.NotificationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendUserNotification(String username, Map<String, Object> payload) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                payload
        );
        System.out.println("📤 Sent private WS message to: " + username + " | Payload: " + payload);
    }

}
