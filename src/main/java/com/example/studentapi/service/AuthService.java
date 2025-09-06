package com.example.studentapi.service;

import com.example.studentapi.model.Teacher;

public interface AuthService {
    String authenticate(String username, String password);
    boolean validateToken(String token);
    Long getTeacherIdFromToken(String token);
    Teacher getCurrentTeacher(String token);
}