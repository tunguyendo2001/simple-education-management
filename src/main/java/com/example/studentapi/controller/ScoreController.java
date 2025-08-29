package com.example.studentapi.controller;

import com.example.studentapi.model.Score;
import com.example.studentapi.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // @Operation(
    //     summary = "Import scores from Excel",
    //     description = "Upload an Excel file (.xls or .xlsx) containing student scores"
    // )
    // @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = {"Content-Type", "Authorization"})
    // public ResponseEntity<List<Score>> importExcel(
    //     @RequestPart("file") MultipartFile file
    // ) {
    //     try {
    //         List<Score> scores = scoreService.importFromExcel(file);
    //         return ResponseEntity.ok(scores);
    //     } catch (IOException e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=scores.xlsx");
            scoreService.exportToExcel(response);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}