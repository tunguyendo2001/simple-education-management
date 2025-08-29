package com.example.studentapi.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String gender;

    private String hometown;

    private LocalDate birthday;

    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

    // Constructors
    public Student() {
    }

    public Student(Long id, String name, String gender, String hometown, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.hometown = hometown;
        this.birthday = birthday;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
}