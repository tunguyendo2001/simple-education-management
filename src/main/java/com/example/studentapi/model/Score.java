package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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

    private Long studentId;
    private Long teacherId;
    private String className;
    private int semester;
    private int year;
    
    // Store as comma-separated string in database
    @Column(name = "ddgtx")
    private String ddgtxString;
    
    private int ddggk;
    private int ddgck;
    private int tbm;
    private String comment;
    private String studentName;
    private String teacherName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Transient getter and setter for List<Integer>
    @Transient
    public List<Integer> getDdgtx() {
        if (ddgtxString == null || ddgtxString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(ddgtxString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    @Transient
    public void setDdgtx(List<Integer> ddgtx) {
        if (ddgtx == null || ddgtx.isEmpty()) {
            this.ddgtxString = null;
        } else {
            this.ddgtxString = ddgtx.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    // Getters and Setters for other fields

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

    // Direct access to string representation (for database operations)
    public String getDdgtxString() {
        return ddgtxString;
    }

    public void setDdgtxString(String ddgtxString) {
        this.ddgtxString = ddgtxString;
    }
}