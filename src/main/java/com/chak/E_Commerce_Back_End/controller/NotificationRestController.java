package com.chak.E_Commerce_Back_End.controller;


import com.chak.E_Commerce_Back_End.dto.notifivation.NotificationDto1;
import com.chak.E_Commerce_Back_End.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationRestController {
    private final NotificationService notificationService;

    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ✅ Fetch all notifications for a user
    @GetMapping("/{username}")
    public ResponseEntity<List<NotificationDto1>> getUserNotifications(@PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUserNotifications(username));
    }

    // ✅ Mark all notifications as seen
    @PostMapping("/mark-seen/{username}")
    public ResponseEntity<Void> markAllAsSeen(@PathVariable String username) {
        notificationService.markAllAsSeen(username);
        return ResponseEntity.ok().build();
    }


    // (Optional) Delete a notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
