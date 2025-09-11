package com.example.studentapi.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers")
public class Teacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "ENUM('MEN','WOMEN')")
    private Gender gender;
    
    @Size(max = 255, message = "Hometown cannot exceed 255 characters")
    @Column(name = "hometown", length = 255)
    private String hometown;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    @Column(name = "username", unique = true, length = 50)
    private String username;
    
    @Size(max = 255, message = "Password hash cannot exceed 255 characters")
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    // Enum for gender
    public enum Gender {
        MEN, WOMEN
    }

    // Constructors
    public Teacher() {}

    public Teacher(String name, Gender gender, String hometown, LocalDate birthday, 
                   String username, String passwordHash, String email) {
        this.name = name;
        this.gender = gender;
        this.hometown = hometown;
        this.birthday = birthday;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.isActive = true;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        LocalDate now = LocalDate.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", hometown='" + hometown + '\'' +
                ", birthday=" + birthday +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}