package com.example.studentapi.service;

import com.example.studentapi.model.Teacher;
import java.util.List;

public interface TeacherService {
    Teacher findById(Long id);
    List<Teacher> findAll();
    Teacher save(Teacher teacher);
    Teacher update(Long id, Teacher teacher);
    void delete(Long id);
}