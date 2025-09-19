package com.chak.E_Commerce_Back_End.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    @ManyToOne(optional = false) // optional=false = non-null
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken() {
    }

    public ConfirmationToken( String token, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {

        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;

        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
// Getters, Setters, Constructors
}

