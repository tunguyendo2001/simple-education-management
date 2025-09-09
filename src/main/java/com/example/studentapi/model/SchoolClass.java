package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "classes")
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;
    
    // Add className field for backward compatibility
    @Column(name = "class_name", unique = true, nullable = false, length = 100)
    private String className;

    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, length = 100)
    private String subject;
    
    // Add isActive field
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many with TeacherClassAssignment
    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherClassAssignment> teacherAssignments;

    // One-to-Many with Scores
    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Score> scores;
    
    // Helper methods
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    // Getter for className (if name is used as className)
    public String getClassName() {
        return className != null ? className : name;
    }
    
    // Setter for className
    public void setClassName(String className) {
        this.className = className;
        if (this.name == null) {
            this.name = className;
        }
    }
}