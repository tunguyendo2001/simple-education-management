package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "semester_schedules")
public class SemesterSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scheduleName;

    @Column(nullable = false)
    private String semester; // 1 or 2

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean isLocked = false;

    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;

    // Helper method to check if current time is within allowed period
    public boolean isCurrentlyAllowed() {
        if (!isActive || isLocked) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDateTime) && !now.isAfter(endDateTime);
    }

    // Helper method to check if schedule should be automatically locked
    public boolean shouldBeLocked() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(endDateTime);
    }
}