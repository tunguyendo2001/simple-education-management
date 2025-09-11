package com.example.studentapi.controller;

import com.example.studentapi.model.Teacher;
import com.example.studentapi.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Teacher Management", description = "APIs for managing teachers")
@Validated
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Operation(summary = "Get all teachers")
    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();
        return ResponseEntity.ok(teachers);
    }

    @Operation(summary = "Get teacher by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.findById(id);
        return teacher != null ? ResponseEntity.ok(teacher) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new teacher")
    @PostMapping
    public ResponseEntity<?> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        try {
            // Check if username already exists
            if (request.getUsername() != null && teacherService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists", "field", "username"));
            }

            // Check if email already exists
            if (request.getEmail() != null && teacherService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists", "field", "email"));
            }

            Teacher teacher = teacherService.createTeacher(request);
            return ResponseEntity.status(201).body(teacher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "An error occurred while creating teacher: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update teacher information")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @Valid @RequestBody UpdateTeacherRequest request) {
        try {
            // Check if teacher exists
            Teacher existingTeacher = teacherService.findById(id);
            if (existingTeacher == null) {
                return ResponseEntity.notFound().build();
            }

            // Check username uniqueness if being updated
            if (request.getUsername() != null && 
                !request.getUsername().equals(existingTeacher.getUsername()) &&
                teacherService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists", "field", "username"));
            }

            // Check email uniqueness if being updated
            if (request.getEmail() != null && 
                !request.getEmail().equals(existingTeacher.getEmail()) &&
                teacherService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists", "field", "email"));
            }

            Teacher updatedTeacher = teacherService.updateTeacher(id, request);
            return ResponseEntity.ok(updatedTeacher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "An error occurred while updating teacher: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete teacher (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        try {
            Teacher teacher = teacherService.findById(id);
            if (teacher == null) {
                return ResponseEntity.notFound().build();
            }

            teacherService.softDelete(id);
            return ResponseEntity.ok()
                .body(Map.of("message", "Teacher deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "An error occurred while deleting teacher: " + e.getMessage()));
        }
    }

    @Operation(summary = "Reactivate teacher")
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateTeacher(@PathVariable Long id) {
        try {
            Teacher teacher = teacherService.findById(id);
            if (teacher == null) {
                return ResponseEntity.notFound().build();
            }

            teacherService.reactivateTeacher(id);
            return ResponseEntity.ok()
                .body(Map.of("message", "Teacher reactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "An error occurred while reactivating teacher: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get active teachers only")
    @GetMapping("/active")
    public ResponseEntity<List<Teacher>> getActiveTeachers() {
        List<Teacher> activeTeachers = teacherService.findActiveTeachers();
        return ResponseEntity.ok(activeTeachers);
    }

    @Operation(summary = "Change teacher password")
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        try {
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "New password is required"));
            }

            if (request.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password must be at least 6 characters long"));
            }

            Teacher teacher = teacherService.findById(id);
            if (teacher == null) {
                return ResponseEntity.notFound().build();
            }

            teacherService.changePassword(id, request.getNewPassword());
            return ResponseEntity.ok()
                .body(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "An error occurred while changing password: " + e.getMessage()));
        }
    }

    // Request DTOs
    public static class CreateTeacherRequest {
        @javax.validation.constraints.NotBlank(message = "Name is required")
        @javax.validation.constraints.Size(max = 255, message = "Name cannot exceed 255 characters")
        private String name;

        private Teacher.Gender gender;
        
        @javax.validation.constraints.Size(max = 255, message = "Hometown cannot exceed 255 characters")
        private String hometown;
        
        private java.time.LocalDate birthday;
        
        @javax.validation.constraints.Size(max = 50, message = "Username cannot exceed 50 characters")
        private String username;
        
        @javax.validation.constraints.NotBlank(message = "Password is required")
        @javax.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        
        @javax.validation.constraints.Email(message = "Email should be valid")
        @javax.validation.constraints.Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Teacher.Gender getGender() { return gender; }
        public void setGender(Teacher.Gender gender) { this.gender = gender; }
        public String getHometown() { return hometown; }
        public void setHometown(String hometown) { this.hometown = hometown; }
        public java.time.LocalDate getBirthday() { return birthday; }
        public void setBirthday(java.time.LocalDate birthday) { this.birthday = birthday; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class UpdateTeacherRequest {
        @javax.validation.constraints.Size(max = 255, message = "Name cannot exceed 255 characters")
        private String name;

        private Teacher.Gender gender;
        
        @javax.validation.constraints.Size(max = 255, message = "Hometown cannot exceed 255 characters")
        private String hometown;
        
        private java.time.LocalDate birthday;
        
        @javax.validation.constraints.Size(max = 50, message = "Username cannot exceed 50 characters")
        private String username;
        
        @javax.validation.constraints.Email(message = "Email should be valid")
        @javax.validation.constraints.Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        private Boolean isActive;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Teacher.Gender getGender() { return gender; }
        public void setGender(Teacher.Gender gender) { this.gender = gender; }
        public String getHometown() { return hometown; }
        public void setHometown(String hometown) { this.hometown = hometown; }
        public java.time.LocalDate getBirthday() { return birthday; }
        public void setBirthday(java.time.LocalDate birthday) { this.birthday = birthday; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public static class ChangePasswordRequest {
        @javax.validation.constraints.NotBlank(message = "New password is required")
        @javax.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
        private String newPassword;

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}