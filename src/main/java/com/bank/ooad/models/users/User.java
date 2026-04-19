package com.bank.ooad.models.users;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String userId;

    protected String name;
    protected String email;
    protected String phone;
    protected String passwordHash;
    protected String role;
    protected LocalDateTime createdAt;

    public void login() {
        System.out.println("User logging in");
    }

    public void logout() {
        System.out.println("User logging out");
    }

    public void register() {
        System.out.println("Registering user");
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile() {
        System.out.println("Updating user profile");
    }

    // Getters and Setters omitted for brevity but conceptually exist
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
