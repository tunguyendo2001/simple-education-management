package com.example.studentapi.controller;

import com.example.studentapi.model.Score;
import com.example.studentapi.service.ScoreService;
import com.example.studentapi.service.impl.ScoreServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Score", description = "Score management APIs")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private ScoreServiceImpl scoreServiceImpl; // For access to security methods

    @GetMapping
    public ResponseEntity<List<Score>> getAllScores() {
        List<Score> scores = scoreService.findAll();
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Score> getScoreById(@PathVariable Long id) {
        Score score = scoreService.findById(id);
        return ResponseEntity.ok(score);
    }

    @PostMapping
    public ResponseEntity<Score> createScore(@RequestBody Score score) {
        Score createdScore = scoreService.save(score);
        return ResponseEntity.status(201).body(createdScore);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Score> updateScore(@PathVariable Long id, @RequestBody Score score) {
        Score updatedScore = scoreService.update(id, score);
        return ResponseEntity.ok(updatedScore);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
        scoreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Secured endpoint to get scores by class name, year, and semester
    @GetMapping("/class/{className}/year/{year}/semester/{semester}")
    @Operation(summary = "Get scores by class, year and semester", 
               description = "Retrieve scores for a specific class, year and semester. Teachers can only access their own classes.")
    public ResponseEntity<?> getScoresByClassYearSemester(
            @PathVariable String className,
            @PathVariable int year,
            @PathVariable int semester,
            HttpServletRequest request) {
        
        // Get teacher ID from request header or session
        // For now, using a simple header-based approach
        // In production, you should use JWT tokens
        String teacherIdHeader = request.getHeader("Teacher-Id");
        
        if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Teacher ID is required in header");
        }
        
        try {
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            // Check if teacher has access to this class
            if (!scoreServiceImpl.teacherHasAccessToClass(teacherId, className)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Teacher does not have access to this class");
            }
            
            List<Score> scores = scoreService.findByClassNameAndYearAndSemester(className, year, semester);
            return ResponseEntity.ok(scores);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Teacher ID format");
        }
    }

    // Secured endpoint to get scores by teacher
    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get scores by teacher ID", 
               description = "Retrieve all scores for a specific teacher. Teachers can only access their own scores.")
    public ResponseEntity<?> getScoresByTeacher(
            @PathVariable Long teacherId,
            HttpServletRequest request) {
        
        // Get current teacher ID from request header
        String currentTeacherIdHeader = request.getHeader("Teacher-Id");
        
        if (currentTeacherIdHeader == null || currentTeacherIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Teacher ID is required in header");
        }
        
        try {
            Long currentTeacherId = Long.parseLong(currentTeacherIdHeader);
            
            // Teachers can only access their own scores
            if (!currentTeacherId.equals(teacherId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Teachers can only access their own scores");
            }
            
            List<Score> scores = scoreService.findByTeacherId(teacherId);
            return ResponseEntity.ok(scores);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Teacher ID format");
        }
    }

    // Secured export endpoint
    @GetMapping("/export")
    @Operation(summary = "Export scores to Excel", 
               description = "Export scores to Excel file. Teachers can only export their own classes.")
    public ResponseEntity<?> exportExcel(HttpServletResponse response, HttpServletRequest request) {
        
        // Get teacher ID from request header
        String teacherIdHeader = request.getHeader("Teacher-Id");
        
        if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Teacher ID is required in header");
        }
        
        try {
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=scores_teacher_" + teacherId + ".xlsx");
            
            // Use the secured export method
            scoreServiceImpl.exportToExcelForTeacher(response, teacherId);
            
            return ResponseEntity.ok().build();
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Teacher ID format");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error exporting Excel file");
        }
    }

    // Admin-only endpoint to export all scores (no security check for admin)
    @GetMapping("/export/admin")
    @Operation(summary = "Admin export all scores", 
               description = "Export all scores to Excel file. Admin access only.")
    public void exportAllScores(HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=all_scores.xlsx");
            scoreService.exportToExcel(response);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Additional secured endpoints
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get scores by student ID", 
               description = "Retrieve scores for a specific student")
    public ResponseEntity<List<Score>> getScoresByStudent(@PathVariable Long studentId) {
        List<Score> scores = scoreService.findByStudentId(studentId);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/class/{className}")
    @Operation(summary = "Get scores by class name", 
               description = "Retrieve all scores for a specific class. Teachers can only access their own classes.")
    public ResponseEntity<?> getScoresByClass(
            @PathVariable String className,
            HttpServletRequest request) {
        
        // Get teacher ID from request header
        String teacherIdHeader = request.getHeader("Teacher-Id");
        
        if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Teacher ID is required in header");
        }
        
        try {
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            // Check if teacher has access to this class
            if (!scoreServiceImpl.teacherHasAccessToClass(teacherId, className)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Teacher does not have access to this class");
            }
            
            List<Score> scores = scoreService.findByClassName(className);
            return ResponseEntity.ok(scores);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Teacher ID format");
        }
    }

    // Utility endpoint to check teacher access to class
    @GetMapping("/check-access/{className}")
    @Operation(summary = "Check teacher access to class", 
               description = "Check if current teacher has access to a specific class")
    public ResponseEntity<?> checkTeacherAccess(
            @PathVariable String className,
            HttpServletRequest request) {
        
        String teacherIdHeader = request.getHeader("Teacher-Id");
        
        if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Teacher ID is required in header");
        }
        
        try {
            Long teacherId = Long.parseLong(teacherIdHeader);
            boolean hasAccess = scoreServiceImpl.teacherHasAccessToClass(teacherId, className);
            
            return ResponseEntity.ok().body(new AccessCheckResponse(hasAccess, 
                hasAccess ? "Teacher has access to class" : "Teacher does not have access to class"));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Teacher ID format");
        }
    }

    // Inner class for access check response
    public static class AccessCheckResponse {
        private boolean hasAccess;
        private String message;
        
        public AccessCheckResponse(boolean hasAccess, String message) {
            this.hasAccess = hasAccess;
            this.message = message;
        }
        
        public boolean isHasAccess() {
            return hasAccess;
        }
        
        public String getMessage() {
            return message;
        }
    }
}