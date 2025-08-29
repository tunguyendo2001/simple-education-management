package com.example.studentapi.service;

import com.example.studentapi.model.Student;
import java.util.List;

public interface StudentService {
    Student findById(Long id);
    List<Student> findAll();
    Student save(Student student);
    Student update(Long id, Student student);
    void delete(Long id);
}