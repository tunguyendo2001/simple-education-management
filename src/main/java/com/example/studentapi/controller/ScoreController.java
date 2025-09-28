package com.example.studentapi.controller;

import com.example.studentapi.model.Score;
import com.example.studentapi.service.ScoreService;
import com.example.studentapi.service.impl.ScoreServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Score> getScoreById(@PathVariable String id) {
        Score score = scoreService.findById(id);
        return ResponseEntity.ok(score);
    }

    // BATCH OPERATIONS
    @PostMapping
    @Operation(summary = "Create multiple scores", 
               description = "Create multiple scores in a single request. All scores must belong to the requesting teacher.",
               parameters = {
                    @Parameter(name = "Teacher-Id", in = ParameterIn.HEADER, 
                               description = "Teacher identifier", required = true,
                               schema = @Schema(type = "string"))
               })
    public ResponseEntity<?> createScores(@Valid @RequestBody List<Score> scores, @RequestHeader("Teacher-Id") String teacherIdHeader, HttpServletRequest request) {
        try {
            if (scores == null || scores.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Score list cannot be empty"));
            }
            
            if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Teacher ID is required in header"));
            }
            
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            List<Score> createdScores = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                
                try {
                    // Set teacher ID if not provided
                    if (score.getTeacherId() == null) {
                        score.setTeacherId(teacherId);
                    }
                    
                    // Verify teacher can only create scores for their own classes
                    if (!score.getTeacherId().equals(teacherId)) {
                        errors.add("Score " + (i + 1) + ": Teachers can only create scores for their own classes");
                        continue;
                    }
                    
                    // Validate teacher has access to the class
                    if (score.getClassName() != null && !scoreServiceImpl.teacherHasAccessToClass(teacherId, score.getClassName(), score.getSubject(), score.getYear(), score.getSemester())) {
                        errors.add("Score " + (i + 1) + ": Teacher does not have access to class " + score.getClassName());
                        continue;
                    }
                    
                    // Auto-calculate TBM
                    score.calculateTbm();
                    
                    Score createdScore = scoreService.save(score);
                    createdScores.add(createdScore);
                    
                } catch (Exception e) {
                    errors.add("Score " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("created", createdScores);
            response.put("createdCount", createdScores.size());
            response.put("totalCount", scores.size());
            
            if (!errors.isEmpty()) {
                response.put("errors", errors);
                response.put("errorCount", errors.size());
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid Teacher ID format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error creating scores: " + e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "Update multiple scores", 
               description = "Update multiple scores in a single request. All scores must belong to the requesting teacher.",
               parameters = {
                    @Parameter(name = "Teacher-Id", in = ParameterIn.HEADER, 
                               description = "Teacher identifier", required = true,
                               schema = @Schema(type = "string"))
               })
    public ResponseEntity<?> updateScores(@Valid @RequestBody List<Score> scores, @RequestHeader("Teacher-Id") String teacherIdHeader, HttpServletRequest request) {
        try {
            if (scores == null || scores.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Score list cannot be empty"));
            }
            
            if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Teacher ID is required in header"));
            }
            
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            List<Score> updatedScores = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                
                try {
                    if (score.getId() == null) {
                        errors.add("Score " + (i + 1) + ": Score ID is required for updates");
                        continue;
                    }
                    
                    // Check if score exists
                    Score existingScore = scoreService.findById(score.getId());
                    if (existingScore == null) {
                        errors.add("Score " + (i + 1) + ": Score not found with ID " + score.getId());
                        continue;
                    }
                    
                    // Verify teacher can only update their own scores
                    if (!existingScore.getTeacherId().equals(teacherId)) {
                        errors.add("Score " + (i + 1) + ": Teachers can only update their own scores");
                        continue;
                    }
                    
                    // Auto-calculate TBM
                    score.calculateTbm();
                    
                    Score updatedScore = scoreService.update(score.getId(), score);
                    updatedScores.add(updatedScore);
                    
                } catch (Exception e) {
                    errors.add("Score " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("updated", updatedScores);
            response.put("updatedCount", updatedScores.size());
            response.put("totalCount", scores.size());
            
            if (!errors.isEmpty()) {
                response.put("errors", errors);
                response.put("errorCount", errors.size());
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid Teacher ID format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error updating scores: " + e.getMessage()));
        }
    }

    // UPSERT OPERATION (Create or Update)
    @PostMapping("/upsert")
    @Operation(summary = "Create or update scores", 
               description = "Create new scores or update existing ones. Use this when you're not sure if scores already exist.",
               parameters = {
                    @Parameter(name = "Teacher-Id", in = ParameterIn.HEADER, 
                               description = "Teacher identifier", required = true,
                               schema = @Schema(type = "string"))
               })
    public ResponseEntity<?> upsertScores(@Valid @RequestBody List<Score> scores, @RequestHeader("Teacher-Id") String teacherIdHeader, HttpServletRequest request) {
        try {
            if (scores == null || scores.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Score list cannot be empty"));
            }
            
            if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Teacher ID is required in header"));
            }
            
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            List<Score> createdScores = new ArrayList<>();
            List<Score> updatedScores = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                
                try {
                    // Set teacher ID if not provided
                    if (score.getTeacherId() == null) {
                        score.setTeacherId(teacherId);
                    }
                    
                    // Verify teacher can only work with their own scores
                    if (!score.getTeacherId().equals(teacherId)) {
                        errors.add("Score " + (i + 1) + ": Teachers can only work with their own scores");
                        continue;
                    }
                    
                    // Auto-calculate TBM
                    score.calculateTbm();
                    
                    if (score.getId() != null) {
                        // Update existing score
                        Score existingScore = scoreService.findById(score.getId());
                        if (existingScore != null && existingScore.getTeacherId().equals(teacherId)) {
                            Score updatedScore = scoreService.update(score.getId(), score);
                            updatedScores.add(updatedScore);
                        } else {
                            errors.add("Score " + (i + 1) + ": Cannot update - score not found or access denied");
                        }
                    } else {
                        // Create new score
                        if (score.getClassName() != null && !scoreServiceImpl.teacherHasAccessToClass(teacherId, score.getClassName(), score.getSubject(), score.getYear(), score.getSemester())) {
                            errors.add("Score " + (i + 1) + ": Teacher does not have access to class " + score.getClassName());
                            continue;
                        }
                        
                        Score createdScore = scoreService.save(score);
                        createdScores.add(createdScore);
                    }
                    
                } catch (Exception e) {
                    errors.add("Score " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("created", createdScores);
            response.put("updated", updatedScores);
            response.put("createdCount", createdScores.size());
            response.put("updatedCount", updatedScores.size());
            response.put("totalCount", scores.size());
            
            if (!errors.isEmpty()) {
                response.put("errors", errors);
                response.put("errorCount", errors.size());
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid Teacher ID format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error processing scores: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a score")
    public ResponseEntity<?> deleteScore(@PathVariable String id, HttpServletRequest request) {
        try {
            // Check if score exists
            Score existingScore = scoreService.findById(id);
            if (existingScore == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Get teacher ID from request header
            String teacherIdHeader = request.getHeader("Teacher-Id");
            if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Teacher ID is required in header"));
            }
            
            Long teacherId = Long.parseLong(teacherIdHeader);
            
            // Verify teacher can only delete their own scores
            if (!existingScore.getTeacherId().equals(teacherId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Teachers can only delete their own scores"));
            }
            
            scoreService.delete(id);
            return ResponseEntity.noContent().build();
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid Teacher ID format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error deleting score: " + e.getMessage()));
        }
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
    //     scoreService.delete(id);
    //     return ResponseEntity.noContent().build();
    // }

    // Secured endpoint to get scores by class name, year, and semester
    @GetMapping("/class/{className}/year/{year}/semester/{semester}/subject/{subject}")
    @Operation(summary = "Get scores by class, year, semester and subject", 
               description = "Retrieve scores for a specific class, year, semester and subject. Teachers can only access their own classes.")
    public ResponseEntity<?> getScoresByClassYearSemester(
            @PathVariable String className,
            @PathVariable int year,
            @PathVariable String semester,
            @PathVariable String subject,
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
            if (!scoreServiceImpl.teacherHasAccessToClass(teacherId, className, subject, year, semester)) {
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
               description = "Export scores to Excel file. Teachers can only export their own classes.",
               parameters = {
                    @Parameter(name = "Teacher-Id", in = ParameterIn.HEADER, 
                               description = "Teacher identifier", required = true,
                               schema = @Schema(type = "string"))
               })
    public ResponseEntity<?> exportExcel(HttpServletResponse response, @RequestHeader("Teacher-Id") String teacherIdHeader, HttpServletRequest request) {
        
        // // Get teacher ID from request header
        // String teacherIdHeader = request.getHeader("Teacher-Id");
        
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

    // @GetMapping("/class/{className}")
    // @Operation(summary = "Get scores by class name", 
    //            description = "Retrieve all scores for a specific class. Teachers can only access their own classes.")
    // public ResponseEntity<?> getScoresByClass(
    //         @PathVariable String className,
    //         HttpServletRequest request) {
        
    //     // Get teacher ID from request header
    //     String teacherIdHeader = request.getHeader("Teacher-Id");
        
    //     if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //             .body("Teacher ID is required in header");
    //     }
        
    //     try {
    //         Long teacherId = Long.parseLong(teacherIdHeader);
            
    //         // Check if teacher has access to this class
    //         if (!scoreServiceImpl.teacherHasAccessToClass(teacherId, className)) {
    //             return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                 .body("Teacher does not have access to this class");
    //         }
            
    //         List<Score> scores = scoreService.findByClassName(className);
    //         return ResponseEntity.ok(scores);
            
    //     } catch (NumberFormatException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //             .body("Invalid Teacher ID format");
    //     }
    // }

    // Utility endpoint to check teacher access to class
    @GetMapping("/check-access/{className}/{year}/{semester}/{subject}")
    @Operation(summary = "Check teacher access to class", 
               description = "Check if current teacher has access to a specific class")
    public ResponseEntity<?> checkTeacherAccess(
            @PathVariable String className,
            @PathVariable int year,
            @PathVariable String semester,
            @PathVariable String subject,
            HttpServletRequest request) {
        
        String teacherIdHeader = request.getHeader("Teacher-Id");
        
        if (teacherIdHeader == null || teacherIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Teacher ID is required in header");
        }
        
        try {
            Long teacherId = Long.parseLong(teacherIdHeader);
            boolean hasAccess = scoreServiceImpl.teacherHasAccessToClass(teacherId, className, subject, year, semester);
            
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