package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "teacher_classes")
public class TeacherClassAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "school_class_id", nullable = false) // Changed from classId to schoolClassId
    private Long schoolClassId; // Renamed to avoid conflict with 'class' keyword

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Optional: Add relationships if needed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;
}