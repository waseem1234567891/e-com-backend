package com.chak.E_Commerce_Back_End.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data

@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private String firstName; // ✅ New field

    @Column(nullable = false)
    private String lastName; // ✅ New field

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore    // 👈 This hides it from JSON serialization
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
   // @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ConfirmationToken> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notificationEntity;






    public User() {
    }

    public User(Long id, String status, String firstName, String lastName, String username, String password, String email, String role, List<Order> orders, List<ConfirmationToken> tokens, List<Address> addresses) {
        this.id = id;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.orders = orders;
        this.tokens = tokens;
        this.addresses = addresses;
    }
    public User(Long id, String status,   String username, String password, String email, String role, List<Order> orders, List<ConfirmationToken> tokens, List<Address> addresses) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.orders = orders;
        this.tokens = tokens;
        this.addresses = addresses;
    }

    public User(Long id, String status, String firstName, String lastName, String username, String password, String role, String email, List<Order> orders, List<ConfirmationToken> tokens, List<Address> addresses, Set<CartItem> cartItems, List<Review> reviews, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.id = id;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.orders = orders;
        this.tokens = tokens;
        this.addresses = addresses;
        this.cartItems = cartItems;
        this.reviews = reviews;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public Long getId() {
        return id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<ConfirmationToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<ConfirmationToken> tokens) {
        this.tokens = tokens;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
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

    public List<NotificationEntity> getNotificationEntity() {
        return notificationEntity;
    }

    public void setNotificationEntity(List<NotificationEntity> notificationEntity) {
        this.notificationEntity = notificationEntity;
    }
}
