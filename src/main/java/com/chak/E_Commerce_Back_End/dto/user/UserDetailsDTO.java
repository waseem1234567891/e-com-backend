package com.chak.E_Commerce_Back_End.dto.user;

import com.chak.E_Commerce_Back_End.dto.order.CustomOrderDto;
import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.model.Address;

import java.time.LocalDateTime;
import java.util.List;

public class UserDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private List<AddressDTO> addresses;
    private List<CustomOrderDto> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

    public List<CustomOrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<CustomOrderDto> orders) {
        this.orders = orders;
    }
}
