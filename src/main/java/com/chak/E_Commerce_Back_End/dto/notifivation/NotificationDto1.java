package com.chak.E_Commerce_Back_End.dto.notifivation;

import com.chak.E_Commerce_Back_End.model.NotificationEntity;

import java.time.LocalDateTime;

public class NotificationDto1 {
    private Long id;
    private String message;
    private String type;
    private String link;
    private boolean seen;
    private LocalDateTime createdAt;

    public static NotificationDto1 fromEntity(NotificationEntity entity) {
        NotificationDto1 dto = new NotificationDto1();
        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setLink(entity.getLink());
        dto.setSeen(entity.isSeen());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
}
