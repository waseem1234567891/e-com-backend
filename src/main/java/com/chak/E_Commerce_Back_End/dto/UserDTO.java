package com.chak.E_Commerce_Back_End.dto;

import lombok.*;

@Data

@Getter
@Setter
public class UserDTO {
    private String status;
    private String username;
    private String password;
    private String email;
    private String role;

    public UserDTO(String status,String username, String password, String email, String role) {
        this.status=status;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public UserDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
