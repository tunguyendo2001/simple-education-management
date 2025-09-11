package com.example.studentapi.service.impl;

import com.example.studentapi.controller.TeacherController.CreateTeacherRequest;
import com.example.studentapi.controller.TeacherController.UpdateTeacherRequest;
import com.example.studentapi.model.Teacher;
import com.example.studentapi.repository.TeacherRepository;
import com.example.studentapi.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findActiveTeachers() {
        return teacherRepository.findAll().stream()
                .filter(teacher -> teacher.getIsActive() != null && teacher.getIsActive())
                .collect(Collectors.toList());
    }

    @Override
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public Teacher update(Long id, Teacher teacher) {
        if (teacherRepository.existsById(id)) {
            teacher.setId(id);
            teacher.setUpdatedAt(LocalDate.now());
            return teacherRepository.save(teacher);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }

    @Override
    public void softDelete(Long id) {
        Teacher teacher = findById(id);
        if (teacher != null) {
            teacher.setIsActive(false);
            teacher.setUpdatedAt(LocalDate.now());
            teacherRepository.save(teacher);
        }
    }

    @Override
    public void reactivateTeacher(Long id) {
        Teacher teacher = findById(id);
        if (teacher != null) {
            teacher.setIsActive(true);
            teacher.setUpdatedAt(LocalDate.now());
            teacherRepository.save(teacher);
        }
    }

    @Override
    public Teacher createTeacher(CreateTeacherRequest request) {
        // Validate required fields
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher name is required");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Create new teacher entity
        Teacher teacher = new Teacher();
        teacher.setName(request.getName().trim());
        teacher.setGender(request.getGender());
        teacher.setHometown(request.getHometown());
        teacher.setBirthday(request.getBirthday());
        
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            teacher.setUsername(request.getUsername().trim());
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            teacher.setEmail(request.getEmail().trim().toLowerCase());
        }

        // Hash the password
        teacher.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        teacher.setIsActive(true);
        teacher.setCreatedAt(LocalDate.now());
        teacher.setUpdatedAt(LocalDate.now());

        return teacherRepository.save(teacher);
    }

    @Override
    public Teacher updateTeacher(Long id, UpdateTeacherRequest request) {
        Teacher existingTeacher = findById(id);
        if (existingTeacher == null) {
            throw new IllegalArgumentException("Teacher not found with id: " + id);
        }

        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            existingTeacher.setName(request.getName().trim());
        }

        if (request.getGender() != null) {
            existingTeacher.setGender(request.getGender());
        }

        if (request.getHometown() != null) {
            existingTeacher.setHometown(request.getHometown());
        }

        if (request.getBirthday() != null) {
            existingTeacher.setBirthday(request.getBirthday());
        }

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            existingTeacher.setUsername(request.getUsername().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            existingTeacher.setEmail(request.getEmail().trim().toLowerCase());
        }

        if (request.getIsActive() != null) {
            existingTeacher.setIsActive(request.getIsActive());
        }

        existingTeacher.setUpdatedAt(LocalDate.now());
        return teacherRepository.save(existingTeacher);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        Teacher teacher = findById(id);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found with id: " + id);
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        teacher.setPasswordHash(passwordEncoder.encode(newPassword));
        teacher.setUpdatedAt(LocalDate.now());
        teacherRepository.save(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return teacherRepository.existsByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return teacherRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return teacherRepository.findByUsername(username.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return teacherRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
    }
}