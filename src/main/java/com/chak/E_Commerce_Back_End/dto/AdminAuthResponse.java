package com.chak.E_Commerce_Back_End.dto;

import lombok.Data;

@Data

public class AdminAuthResponse {
    private String token;
    private Integer userId;
    private String userName;

    public AdminAuthResponse() {
    }

    public AdminAuthResponse(String token, Integer userId,String userName) {
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

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public Integer getUserId() {
        return userId;
    }
}
