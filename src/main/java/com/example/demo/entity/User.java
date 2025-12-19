package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    @NotBlank(message = "Department is required")
    private String department;

    private String role;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String fullName, String email, String department,
                String role, String password, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.role = role;
        this.password = password;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "USER";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { 
           return id; 
    }
    public void setId(Long id) { 
           this.id = id; 
    }

    public String getFullName() { 
            return fullName; 
    }
    public void setFullName(String fullName) { 
            this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
