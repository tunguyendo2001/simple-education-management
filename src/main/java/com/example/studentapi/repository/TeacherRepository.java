package com.example.studentapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.studentapi.model.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // Additional query methods can be defined here if needed
}