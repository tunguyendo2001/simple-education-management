package com.example.studentapi.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String hometown;

    private LocalDate birthday;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

    public enum Gender {
        MEN, WOMEN
    }
}