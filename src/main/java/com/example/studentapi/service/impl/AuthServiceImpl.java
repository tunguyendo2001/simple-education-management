package com.example.studentapi.service.impl;

import com.example.studentapi.model.Teacher;
import com.example.studentapi.repository.TeacherRepository;
import com.example.studentapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TeacherRepository teacherRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // In production, store this securely
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("${jwt.expiration:300000}")
    private long EXPIRATION_TIME;

    @Override
    public String authenticate(String username, String password) {
        Optional<Teacher> teacherOpt = teacherRepository.findByUsername(username);
        
        if (teacherOpt.isEmpty()) {
            return null; // User not found
        }
        
        Teacher teacher = teacherOpt.get();
        
        // Check if teacher is active
        if (!teacher.getIsActive()) {
            return null; // Teacher is not active
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, teacher.getPasswordHash())) {
            return null; // Wrong password
        }
        
        // Generate JWT token
        return generateToken(teacher);
    }

    private String generateToken(Teacher teacher) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .setSubject(teacher.getId().toString())
                .claim("username", teacher.getUsername())
                .claim("teacherName", teacher.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Long getTeacherIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Teacher getCurrentTeacher(String token) {
        Long teacherId = getTeacherIdFromToken(token);
        if (teacherId != null) {
            return teacherRepository.findById(teacherId).orElse(null);
        }
        return null;
    }
}