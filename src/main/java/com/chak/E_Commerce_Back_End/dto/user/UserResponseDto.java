package com.chak.E_Commerce_Back_End.dto.user;

import com.chak.E_Commerce_Back_End.model.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserResponseDto {

    private Long id;
    private String status ;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;

    public UserResponseDto() {
    }

    public UserResponseDto(User user) {
        this.id=user.getId();
        this.status=user.getStatus();
        this.username=user.getUsername();
        this.email=user.getEmail();
        this.role=user.getRole();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
