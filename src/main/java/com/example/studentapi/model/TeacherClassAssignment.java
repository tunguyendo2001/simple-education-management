package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "teacher_class_assignments")
public class TeacherClassAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classEntity;

    @Column(nullable = false)
    private String subject; // e.g., "Tin học", "Toán", "Văn"

    @Column(nullable = false)
    private int academicYear;

    @Column(nullable = false)
    private String semester; // "1", "2", or "BOTH"

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean isPrimaryTeacher = false; // Is this teacher the homeroom teacher?

    @Enumerated(EnumType.STRING)
    private AssignmentRole role = AssignmentRole.SUBJECT_TEACHER;

    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String assignedBy; // Who made this assignment

    public enum AssignmentRole {
        HOMEROOM_TEACHER,    // Giáo viên chủ nhiệm
        SUBJECT_TEACHER,     // Giáo viên bộ môn
        ASSISTANT_TEACHER    // Giáo viên phụ trách
    }

    // Helper method to check if teacher can access this class
    public boolean canTeacherAccess(Long teacherId) {
        return this.teacher.getId().equals(teacherId) && this.isActive;
    }
}