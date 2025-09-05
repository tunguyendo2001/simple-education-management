package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;
    
    @Column(nullable = false)
    private Long teacherId;
    
    // Add normalized class reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Class classEntity;
    
    // Keep denormalized className for backward compatibility and performance
    @Column(nullable = false)
    private String className;
    
    @Column(nullable = false)
    private int semester;
    
    @Column(nullable = false)
    private int year;
    
    // Store the list using @ElementCollection
    @ElementCollection
    @CollectionTable(name = "score_ddgtx", joinColumns = @JoinColumn(name = "score_id"))
    @Column(name = "ddgtx_value")
    private List<Integer> ddgtx;
    
    private int ddggk;
    private int ddgck;
    private int tbm;
    private String comment;
    
    // Denormalized fields for performance
    private String studentName;
    private String teacherName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters remain the same
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Class getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(Class classEntity) {
        this.classEntity = classEntity;
        // Automatically sync className when classEntity is set
        if (classEntity != null) {
            this.className = classEntity.getClassName();
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<Integer> getDdgtx() {
        return ddgtx;
    }

    public void setDdgtx(List<Integer> ddgtx) {
        this.ddgtx = ddgtx;
    }

    public int getDdggk() {
        return ddggk;
    }

    public void setDdggk(int ddggk) {
        this.ddggk = ddggk;
    }

    public int getDdgck() {
        return ddgck;
    }

    public void setDdgck(int ddgck) {
        this.ddgck = ddgck;
    }

    public int getTbm() {
        return tbm;
    }

    public void setTbm(int tbm) {
        this.tbm = tbm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}