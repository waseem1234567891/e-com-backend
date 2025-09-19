package com.chak.E_Commerce_Back_End.dto.user;

import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.model.Address;
import com.chak.E_Commerce_Back_End.model.Order;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String role;
    private String status;
    private List<Address> addresses;
    private List<OrderResponseDTO> orders;

    public DashboardResponse(Long id,String firstName,String lastName,String userName, String email, String role, String status, List<Address> addresses, List<OrderResponseDTO> orders) {
       this.id=id;
       this.firstName=firstName;
       this.lastName=lastName;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.addresses = addresses;
        this.orders = orders;
    }

    public DashboardResponse() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<OrderResponseDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponseDTO> orders) {
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
