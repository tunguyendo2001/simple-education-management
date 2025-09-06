package com.example.studentapi.controller;

import com.example.studentapi.model.Teacher;
import com.example.studentapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Teacher login", description = "Authenticate teacher and return JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(false, "Username and password are required", null, null));
        }
        
        String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (token != null) {
            Teacher teacher = authService.getCurrentTeacher(token);
            return ResponseEntity.ok(new AuthResponse(true, "Login successful", token, teacher));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, "Invalid username or password", null, null));
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token and return teacher info")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        
        String token = extractTokenFromRequest(request);
        
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, "No token provided", null, null));
        }
        
        if (authService.validateToken(token)) {
            Teacher teacher = authService.getCurrentTeacher(token);
            return ResponseEntity.ok(new AuthResponse(true, "Token is valid", token, teacher));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, "Invalid or expired token", null, null));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Teacher logout", description = "Logout teacher (client should discard token)")
    public ResponseEntity<?> logout() {
        // In a stateless JWT implementation, logout is handled client-side
        // The client should simply discard the token
        return ResponseEntity.ok(new AuthResponse(true, "Logout successful", null, null));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Inner classes for request/response
    public static class LoginRequest {
        private String username;
        private String password;
        
        // Constructors
        public LoginRequest() {}
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class AuthResponse {
        private boolean success;
        private String message;
        private String token;
        private Teacher teacher;
        
        public AuthResponse(boolean success, String message, String token, Teacher teacher) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.teacher = teacher;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getToken() { return token; }
        public Teacher getTeacher() { return teacher; }
    }
}