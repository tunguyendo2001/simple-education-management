package com.example.studentapi.controller;

import com.example.studentapi.model.Score;
import com.example.studentapi.service.ScoreService;
import com.example.studentapi.service.impl.ScoreServiceImpl;
import com.example.studentapi.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Score", description = "Score management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SecureScoreController {

    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private ScoreServiceImpl scoreServiceImpl;

    @GetMapping("/my-scores")
    @Operation(summary = "Get current teacher's scores", 
               description = "Retrieve all scores for the authenticated teacher")
    public ResponseEntity<List<Score>> getMyScores(HttpServletRequest request) {
        Long teacherId = SecurityUtils.getCurrentTeacherId(request);
        List<Score> scores = scoreService.findByTeacherId(teacherId);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/class/{className}")
    @Operation(summary = "Get scores by class name", 
               description = "Retrieve scores for a specific class. Only accessible by assigned teacher.")
    public ResponseEntity<?> getScoresByClass(
            @PathVariable String className,
            HttpServletRequest request) {
        
        Long teacherId = SecurityUtils.getCurrentTeacherId(request);
        
        // Check if teacher has access to this class
        if (!scoreServiceImpl.teacherHasAccessToClass(teacherId, className)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied: You are not assigned to this class");
        }
        
        List<Score> scores = scoreService.findByClassName(className);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/export")
    @Operation(summary = "Export my classes to Excel", 
               description = "Export scores for all classes assigned to the authenticated teacher")
    public void exportMyScores(HttpServletResponse response, HttpServletRequest request) {
        try {
            Long teacherId = SecurityUtils.getCurrentTeacherId(request);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=my_scores.xlsx");
            
            scoreServiceImpl.exportToExcelForTeacher(response, teacherId);
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Standard CRUD operations with security
    @PostMapping
    @Operation(summary = "Create new score", description = "Create a new score record")
    public ResponseEntity<Score> createScore(@RequestBody Score score, HttpServletRequest request) {
        Long teacherId = SecurityUtils.getCurrentTeacherId(request);
        
        // Ensure the score is assigned to the current teacher
        score.setTeacherId(teacherId);
        
        Score createdScore = scoreService.save(score);
        return ResponseEntity.status(201).body(createdScore);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update score", description = "Update an existing score record")
    public ResponseEntity<?> updateScore(@PathVariable Long id, @RequestBody Score score, HttpServletRequest request) {
        Long teacherId = SecurityUtils.getCurrentTeacherId(request);
        
        // Check if the score belongs to the current teacher
        Score existingScore = scoreService.findById(id);
        if (existingScore == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (!existingScore.getTeacherId().equals(teacherId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied: You can only update your own scores");
        }
        
        score.setTeacherId(teacherId); // Ensure teacher ID doesn't change
        Score updatedScore = scoreService.update(id, score);
        return ResponseEntity.ok(updatedScore);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete score", description = "Delete a score record")
    public ResponseEntity<?> deleteScore(@PathVariable Long id, HttpServletRequest request) {
        Long teacherId = SecurityUtils.getCurrentTeacherId(request);
        
        // Check if the score belongs to the current teacher
        Score existingScore = scoreService.findById(id);
        if (existingScore == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (!existingScore.getTeacherId().equals(teacherId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied: You can only delete your own scores");
        }
        
        scoreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}