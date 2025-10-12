package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationTriggerController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Autowired
    public NotificationTriggerController(SimpMessagingTemplate messagingTemplate,
                                         NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    /**
     * ✅ Send a private notification to the authenticated user.
     * Uses the REST Principal to match the WebSocket session automatically.
     */
    @PostMapping("/send")
    public Map<String, String> sendNotification(@RequestBody Map<String, String> payload, Principal principal) {
        String message = payload.get("message");

        if (principal == null) {
            return Map.of("status", "error", "message", "No authenticated user found!");
        }
        if (message == null || message.isBlank()) {
            return Map.of("status", "error", "message", "Message is required!");
        }

        String username = principal.getName(); // This must match WebSocket Principal
        System.out.println("📤 Sending private notification to: " + username);

        // Send as JSON payload to WebSocket
        notificationService.sendUserNotification(username, Map.of("message", message, "type", "info"));

        return Map.of("status", "success", "message", "Notification sent to " + username);
    }

    /**
     * ✅ Broadcast a message to all connected clients.
     */
    @PostMapping("/broadcast")
    public Map<String, String> broadcast(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        if (message == null || message.isBlank()) {
            return Map.of("status", "error", "message", "Message is required!");
        }

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                Map.of("message", message, "type", "info")
        );
        System.out.println("🌍 Broadcast notification sent: " + message);
        return Map.of("status", "success", "message", "Broadcast notification sent!");
    }
}
