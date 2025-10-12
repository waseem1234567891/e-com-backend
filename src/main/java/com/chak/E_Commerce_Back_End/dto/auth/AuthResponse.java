package com.chak.E_Commerce_Back_End.dto.auth;

import lombok.Data;

@Data

public class AuthResponse {
    private String token;
    private Long userId;
    private String userName;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId,String userName,String role) {
        this.token = token;
        this.userId = userId;
        this.userName=userName;
        this.role=role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
