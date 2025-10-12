package com.chak.E_Commerce_Back_End.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestNotificationController {

    private final NotificationWebSocketController notificationController;

    public TestNotificationController(NotificationWebSocketController notificationController) {
        this.notificationController = notificationController;
    }

    // Test endpoint to send a private notification
    @GetMapping("/test-notification")
    public String sendTestNotification(@RequestParam String username) {
        notificationController.sendUserNotification(
                username,
                "✅ Test notification for user: " + username
        );
        return "Notification sent to " + username;
    }

    // Optional: send global notification
    @GetMapping("/test-global-notification")
    public String sendGlobalNotification() {
        notificationController.sendGlobalNotification("🌍 Global test notification!");
        return "Global notification sent";
    }
}
