package com.chak.E_Commerce_Back_End.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Send global notifications
    public void sendGlobalNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notifications",
                Map.of("message", message, "type", "info"));
    }

    // Send private notifications to WebSocket session of the Principal
    public void sendUserNotification(String username, String message) {
        Map<String,Object> payload = Map.of("message", message, "type", "info");

        messagingTemplate.convertAndSendToUser(
                username,                 // matches WebSocket Principal
                "/queue/notifications",   // client subscribes here
                payload
        );

        System.out.println("📤 Sent private WS message to: " + username + " | Payload: " + payload);
    }
}
