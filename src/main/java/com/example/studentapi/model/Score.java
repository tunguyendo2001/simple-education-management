package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @Column(name = "id", length = 500)
    private String id;

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
    private Integer year;
    
    // Store as comma-separated string in database
    @Column(name = "ddgtx", columnDefinition = "TEXT")
    private String ddgtx;
    
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

    // Method to generate custom ID
    @PrePersist
    @PreUpdate
    public void generateCustomId() {
        if (id == null || id.trim().isEmpty()) {
            this.id = generateScoreId();
        }
        
        // Auto-calculate TBM
        calculateTbm();
        
        // Auto-map className to classId if needed
        if (classId == null && className != null) {
            // This would require injecting ClassRepository or using static access
            // We'll handle this in the service layer instead
        }
    }

    /**
     * Generate unique ID based on business logic
     * Format: teacherId_studentId_className_subject_year_semester
     */
    public String generateScoreId() {
        if (teacherId == null || studentId == null || className == null || 
            subject == null || year == null || semester == null) {
            throw new IllegalStateException("Required fields (teacherId/studentId/className/subject/year/semester) are missing");
        }
        
        // Clean the strings to avoid issues with special characters
        String cleanClassName = cleanString(className);
        String cleanSubject = cleanString(subject);
        
        return String.format("%d_%d_%s_%s_%d_%s", 
            teacherId, studentId, year, semester, cleanClassName, cleanSubject);
    }

    /**
     * Clean string for use in ID (remove special characters, spaces, etc.)
     */
    private String cleanString(String input) {
        if (input == null) return "";
        // Remove Vietnamese diacritics and special characters, convert to lowercase
        String cleaned = input
            .toLowerCase()
            .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
            .replaceAll("[èéẹẻẽêềếệểễ]", "e")
            .replaceAll("[ìíịỉĩ]", "i")
            .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
            .replaceAll("[ùúụủũưừứựửữ]", "u")
            .replaceAll("[ỳýỵỷỹ]", "y")
            .replaceAll("[đ]", "d")
            .replaceAll("[^a-zA-Z0-9]", "");
        return cleaned;
    }

    // Static method to create score ID from parameters
    public static String createScoreId(Long teacherId, Long studentId, String className, 
                                      String subject, Integer year, String semester) {
        String cleanClassName = className != null ? className.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() : "";
        String cleanSubject = subject != null ? subject.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() : "";
        
        return String.format("%d_%d_%s_%s_%d_%s", 
            teacherId, studentId, cleanClassName, cleanSubject, year, semester);
    }

    // Transient getter and setter for List<Integer>
    @Transient
    @JsonIgnore
    public List<Integer> getDdgtxList() {
        if (ddgtx == null || ddgtx.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return Arrays.stream(ddgtx.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }

    @Transient
    @JsonIgnore
    public void setDdgtxList(List<Integer> ddgtx) {
        if (ddgtx == null || ddgtx.isEmpty()) {
            this.ddgtx = "";
        } else {
            this.ddgtx = ddgtx.stream()
                    .filter(score -> score >= 0 && score <= 10) // Validate scores
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    // Helper method to calculate average of regular scores
    @Transient
    public double getAverageDdgtx() {
        List<Integer> scores = getDdgtxList();
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
        
        this.id = generateScoreId();
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
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
    
    public String getDdgtx() { return ddgtx; }
    public void setDdgtx(String ddgtx) { this.ddgtx = ddgtx; }
}