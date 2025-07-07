package com.chak.E_Commerce_Back_End.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class AuthResponse {
    private String token;
    private Long userId;
    private String userName;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId,String userName) {
        this.token = token;
        this.userId = userId;
        this.userName=userName;
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
}
