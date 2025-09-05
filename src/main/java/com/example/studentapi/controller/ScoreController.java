package com.example.studentapi.controller;

import com.example.studentapi.model.Score;
import com.example.studentapi.service.AuthorizationService;
import com.example.studentapi.service.ScoreService;
import com.example.studentapi.service.SemesterScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Score", description = "Score management APIs with authorization")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private SemesterScheduleService scheduleService;
    
    @Autowired
    private AuthorizationService authorizationService;

    @Operation(summary = "Get all scores (filtered by teacher authorization)")
    @GetMapping
    public ResponseEntity<List<Score>> getAllScores(
            @Parameter(description = "Teacher ID for authorization", required = true)
            @RequestParam Long teacherId) {
        
        List<Score> allScores = scoreService.findAll();
        
        // Filter scores based on teacher authorization
        List<Score> authorizedScores = allScores.stream()
            .filter(score -> authorizationService.canTeacherAccessClass(
                teacherId, score.getClassName(), score.getYear(), String.valueOf(score.getSemester())))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(authorizedScores);
    }

    @Operation(summary = "Get scores by class (with authorization)")
    @GetMapping("/by-class")
    public ResponseEntity<?> getScoresByClass(
            @RequestParam Long teacherId,
            @RequestParam String className,
            @RequestParam int academicYear,
            @RequestParam String semester) {
        
        // Check authorization first
        if (!authorizationService.canTeacherAccessClass(teacherId, className, academicYear, semester)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access denied",
                "message", "Teacher is not authorized to access scores for class " + className,
                "teacherId", teacherId,
                "className", className
            ));
        }
        
        List<Score> scores = scoreService.findByClassNameAndYearAndSemester(className, academicYear, Integer.parseInt(semester));
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScoreById(
            @PathVariable Long id,
            @RequestParam Long teacherId) {
        
        Score score = scoreService.findById(id);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if teacher can access this score
        if (!authorizationService.canTeacherModifyScore(teacherId, id)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access denied",
                "message", "Teacher is not authorized to access this score",
                "scoreId", id,
                "teacherId", teacherId
            ));
        }
        
        return ResponseEntity.ok(score);
    }

    @Operation(summary = "Create a new score (with schedule and authorization validation)")
    @PostMapping
    public ResponseEntity<?> createScore(
            @RequestBody Score score,
            @Parameter(description = "Teacher ID for authorization", required = true)
            @RequestParam Long teacherId) {
        
        // Set teacher ID in the score
        score.setTeacherId(teacherId);
        
        // Check teacher authorization first
        if (!authorizationService.canTeacherAccessClass(
                teacherId, score.getClassName(), score.getYear(), String.valueOf(score.getSemester()))) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access denied",
                "message", "Teacher is not authorized to create scores for class " + score.getClassName(),
                "teacherId", teacherId,
                "className", score.getClassName()
            ));
        }
        
        // Check subject authorization
        if (!authorizationService.isTeacherAuthorizedForSubject(
                teacherId, score.getClassName(), "Tin học", score.getYear(), String.valueOf(score.getSemester()))) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Subject access denied",
                "message", "Teacher is not authorized to teach 'Tin học' for class " + score.getClassName(),
                "teacherId", teacherId,
                "subject", "Tin học"
            ));
        }
        
        // Check schedule permission
        boolean isAllowed = scheduleService.isScoreEntryAllowed(
            score.getSemester(), score.getYear(), score.getClassName());
        
        if (!isAllowed) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Schedule restriction",
                "message", "Score entry period has expired or not yet started for class " + score.getClassName(),
                "className", score.getClassName(),
                "semester", score.getSemester(),
                "year", score.getYear()
            ));
        }
        
        Score createdScore = scoreService.save(score);
        return ResponseEntity.status(201).body(createdScore);
    }

    @Operation(summary = "Update score (with authorization validation)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateScore(
            @PathVariable Long id, 
            @RequestBody Score score,
            @RequestParam Long teacherId) {
        
        Score existingScore = scoreService.findById(id);
        if (existingScore == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if teacher can modify this score
        if (!authorizationService.canTeacherModifyScore(teacherId, id)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access denied",
                "message", "Teacher is not authorized to modify this score",
                "scoreId", id,
                "teacherId", teacherId
            ));
        }
        
        // Use existing score's class info for validation
        String className = score.getClassName() != null ? score.getClassName() : existingScore.getClassName();
        int semester = score.getSemester() != 0 ? score.getSemester() : existingScore.getSemester();
        int year = score.getYear() != 0 ? score.getYear() : existingScore.getYear();
        
        // Check schedule permission
        boolean isAllowed = scheduleService.isScoreEntryAllowed(semester, year, className);
        
        if (!isAllowed) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Schedule restriction",
                "message", "Score modification period has expired for class " + className,
                "className", className,
                "semester", semester,
                "year", year
            ));
        }
        
        // Ensure teacher ID consistency
        score.setTeacherId(teacherId);
        Score updatedScore = scoreService.update(id, score);
        return ResponseEntity.ok(updatedScore);
    }

    @Operation(summary = "Delete score (with authorization validation)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScore(
            @PathVariable Long id,
            @RequestParam Long teacherId) {
        
        Score existingScore = scoreService.findById(id);
        if (existingScore == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if teacher can delete this score
        if (!authorizationService.canTeacherModifyScore(teacherId, id)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access denied",
                "message", "Teacher is not authorized to delete this score",
                "scoreId", id,
                "teacherId", teacherId
            ));
        }
        
        // Check schedule permission
        boolean isAllowed = scheduleService.isScoreEntryAllowed(
            existingScore.getSemester(), existingScore.getYear(), existingScore.getClassName());
        
        if (!isAllowed) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Schedule restriction",
                "message", "Score deletion period has expired",
                "className", existingScore.getClassName(),
                "semester", existingScore.getSemester(),
                "year", existingScore.getYear()
            ));
        }
        
        scoreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get teacher's accessible classes")
    @GetMapping("/teacher-classes")
    public ResponseEntity<List<String>> getTeacherClasses(
            @RequestParam Long teacherId,
            @RequestParam int academicYear,
            @RequestParam String semester) {
        
        List<String> accessibleClasses = authorizationService.getTeacherAccessibleClasses(
            teacherId, academicYear, semester);
        
        return ResponseEntity.ok(accessibleClasses);
    }

    @GetMapping("/export")
    public void exportExcel(
            HttpServletResponse response,
            @RequestParam Long teacherId) {
        try {
            // You might want to filter export data based on teacher authorization
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=scores.xlsx");
            scoreService.exportToExcel(response);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}