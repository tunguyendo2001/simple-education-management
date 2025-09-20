package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id")
    private Long studentId;
    
    @NotNull(message = "Teacher ID is required")
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "class_id") // Keep this for backward compatibility
    private Long classId;
    
    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;
    
    // ADD MISSING SUBJECT FIELD
    @NotBlank(message = "Subject is required")
    @Column(name = "subject", nullable = false, length = 100)
    private String subject;
    
    @NotBlank(message = "Semester is required")
    @Column(nullable = false, length = 10)
    private String semester;
    
    @NotNull(message = "Year is required")
    @Column(nullable = false)
    private int year;
    
    // Store as comma-separated string in database
    @Column(name = "ddgtx", columnDefinition = "TEXT")
    private String ddgtxString;
    
    @Min(value = 0, message = "Score must be between 0 and 10")
    @Max(value = 10, message = "Score must be between 0 and 10")
    @Column(name = "ddggk")
    private Integer ddggk = 0;
    
    @Min(value = 0, message = "Score must be between 0 and 10")
    @Max(value = 10, message = "Score must be between 0 and 10")
    @Column(name = "ddgck")
    private Integer ddgck = 0;
    
    @Min(value = 0, message = "Average score must be between 0 and 10")
    @Max(value = 10, message = "Average score must be between 0 and 10")
    @Column(name = "tbm")
    private Double tbm = 0.0;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "student_name", length = 255)
    private String studentName;
    
    @Column(name = "teacher_name", length = 255)
    private String teacherName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;

    // Transient getter and setter for List<Integer>
    @Transient
    public List<Integer> getDdgtx() {
        if (ddgtxString == null || ddgtxString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return Arrays.stream(ddgtxString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }

    @Transient
    public void setDdgtx(List<Integer> ddgtx) {
        if (ddgtx == null || ddgtx.isEmpty()) {
            this.ddgtxString = "";
        } else {
            this.ddgtxString = ddgtx.stream()
                    .filter(score -> score >= 0 && score <= 10) // Validate scores
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    // Helper method to calculate average of regular scores
    @Transient
    public double getAverageDdgtx() {
        List<Integer> scores = getDdgtx();
        if (scores.isEmpty()) {
            return 0.0;
        }
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    // Helper method to automatically calculate TBM based on Vietnamese grading system
    @Transient
    public void calculateTbm() {
        double avgTx = getAverageDdgtx();
        int gk = ddggk != null ? ddggk : 0;
        int ck = ddgck != null ? ddgck : 0;
        
        // Vietnamese formula: (Tx + 2*GK + 3*CK) / 6
        if (avgTx > 0 || gk > 0 || ck > 0) {
            this.tbm = Math.round(((avgTx + 2 * gk + 3 * ck) / 6) * 10.0) / 10.0;
        } else {
            this.tbm = 0.0;
        }
    }

    // Constructors
    public Score() {}

    public Score(Long studentId, Long teacherId, String className, String subject, 
                 String semester, int year) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.className = className;
        this.subject = subject;
        this.semester = semester;
        this.year = year;
        this.ddggk = 0;
        this.ddgck = 0;
        this.tbm = 0.0;
    }

    // Getters and Setters (keeping existing ones and adding new ones)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getTbm() {
        return tbm;
    }

    public void setTbm(Double tbm) {
        this.tbm = tbm;
    }

    public Integer getDdggk() {
        return ddggk;
    }

    public void setDdggk(Integer ddggk) {
        this.ddggk = ddggk;
    }

    public Integer getDdgck() {
        return ddgck;
    }

    public void setDdgck(Integer ddgck) {
        this.ddgck = ddgck;
    }

    // Keep all existing getters and setters...
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public String getDdgtxString() { return ddgtxString; }
    public void setDdgtxString(String ddgtxString) { this.ddgtxString = ddgtxString; }
}