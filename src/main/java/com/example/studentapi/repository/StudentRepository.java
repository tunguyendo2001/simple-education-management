package com.example.studentapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.studentapi.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Additional query methods can be defined here if needed
}