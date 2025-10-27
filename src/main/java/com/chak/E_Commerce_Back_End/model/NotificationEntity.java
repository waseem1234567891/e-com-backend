package com.chak.E_Commerce_Back_End.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 The username (or could be userId if you prefer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 📨 Notification message text
    @Column(nullable = false, length = 500)
    private String message;

    // 🔗 Optional link (could be internal route or external URL)
    private String link;

    // 🎨 Type for styling on frontend: success, error, info, warning, etc.
    @Column(nullable = false)
    private String type = "info";

    // 👁️ Whether the user has opened or seen it
    @Column(nullable = false)
    private boolean seen = false;

    // 🕒 When the notification was created
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🧱 Optional: if you want to store who sent it (admin)
    private String sender;

    // === Getters & Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}