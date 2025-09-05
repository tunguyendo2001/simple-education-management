package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "classes")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String className; // e.g., "10A2"

    @Column(nullable = false)
    private int grade; // e.g., 10, 11, 12

    @Column(nullable = false)
    private String section; // e.g., "A2"

    @Column(nullable = false)
    private int academicYear; // e.g., 2024

    @Column(nullable = false)
    private String semester; // "1", "2", or "BOTH"

    private String description;

    @Column(nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships will be handled through junction tables
}