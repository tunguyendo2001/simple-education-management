package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "class_id", nullable = false)
    private Long schoolClassId;
    
    // Add missing fields
    @Column(nullable = false, length = 100)
    private String subject;
    
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;
    
    @Column(nullable = false, length = 20)
    private String semester; // "1", "2", or "BOTH"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_role", nullable = false)
    private AssignmentRole role = AssignmentRole.SUBJECT_TEACHER;
    
    @Column(name = "is_primary_teacher", nullable = false)
    private Boolean isPrimaryTeacher = false;
    
    @Column(name = "assigned_by", length = 100)
    private String assignedBy;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;
    
    // Add classEntity for backward compatibility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass classEntity;
    
    // Enum for assignment roles
    public enum AssignmentRole {
        PRIMARY_TEACHER,
        SUBJECT_TEACHER,
        ASSISTANT_TEACHER,
        SUBSTITUTE_TEACHER
    }
    
    // Helper methods
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    public boolean isPrimary() {
        return isPrimaryTeacher != null && isPrimaryTeacher;
    }
    
    // Getters and setters for missing methods
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public Integer getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public AssignmentRole getRole() {
        return role;
    }
    
    public void setRole(AssignmentRole role) {
        this.role = role;
    }
    
    public Boolean isPrimaryTeacher() {
        return isPrimaryTeacher;
    }
    
    public void setPrimaryTeacher(Boolean primaryTeacher) {
        this.isPrimaryTeacher = primaryTeacher;
    }
    
    public String getAssignedBy() {
        return assignedBy;
    }
    
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setActive(Boolean active) {
        this.isActive = active;
    }
    
    // Getter for classEntity (compatibility)
    public SchoolClass getClassEntity() {
        return schoolClass;
    }
    
    public void setClassEntity(SchoolClass classEntity) {
        this.schoolClass = classEntity;
        this.schoolClassId = classEntity != null ? classEntity.getId() : null;
    }
}