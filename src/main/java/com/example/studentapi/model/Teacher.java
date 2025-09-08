package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String hometown;

    private LocalDate birthday;

    // Authentication fields
    @Column(unique = true, length = 50)
    private String username;

    @JsonIgnore // Don't expose password in JSON responses
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(unique = true, length = 100)
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    // Many-to-Many relationship with Classes (if using the new class structure)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "teacher_classes",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @JsonIgnore // Prevent circular reference in JSON serialization
    private List<SchoolClass> classes;

    // One-to-Many relationship with Scores
    @OneToMany(mappedBy = "teacherId", fetch = FetchType.LAZY)
    @JsonIgnore // Prevent circular reference and performance issues
    private List<Score> scores;

    public enum Gender {
        MEN, WOMEN
    }

    // Helper methods
    public boolean isAccountActive() {
        return isActive != null && isActive;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // Override toString to avoid exposing sensitive information
    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}