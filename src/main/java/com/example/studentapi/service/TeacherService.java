package com.example.studentapi.service;

import com.example.studentapi.controller.TeacherController.CreateTeacherRequest;
import com.example.studentapi.controller.TeacherController.UpdateTeacherRequest;
import com.example.studentapi.model.Teacher;
import java.util.List;

public interface TeacherService {
    Teacher findById(Long id);
    List<Teacher> findAll();
    List<Teacher> findActiveTeachers();
    Teacher save(Teacher teacher);
    Teacher update(Long id, Teacher teacher);
    void delete(Long id);
    void softDelete(Long id);
    void reactivateTeacher(Long id);
    
    // New methods for the updated API
    Teacher createTeacher(CreateTeacherRequest request);
    Teacher updateTeacher(Long id, UpdateTeacherRequest request);
    void changePassword(Long id, String newPassword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Teacher findByUsername(String username);
    Teacher findByEmail(String email);
}