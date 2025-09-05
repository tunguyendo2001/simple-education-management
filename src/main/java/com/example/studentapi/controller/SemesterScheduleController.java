package com.example.studentapi.controller;

import com.example.studentapi.model.SemesterSchedule;
import com.example.studentapi.service.SemesterScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/semester-schedules")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Semester Schedule", description = "APIs for managing semester result entry schedules")
public class SemesterScheduleController {

    @Autowired
    private SemesterScheduleService scheduleService;

    @Operation(summary = "Create a new semester schedule")
    @PostMapping
    public ResponseEntity<SemesterSchedule> createSchedule(@Valid @RequestBody SemesterSchedule schedule) {
        try {
            SemesterSchedule createdSchedule = scheduleService.createSchedule(schedule);
            return ResponseEntity.status(201).body(createdSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get all semester schedules")
    @GetMapping
    public ResponseEntity<List<SemesterSchedule>> getAllSchedules() {
        List<SemesterSchedule> schedules = scheduleService.findAll();
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Get active semester schedules only")
    @GetMapping("/active")
    public ResponseEntity<List<SemesterSchedule>> getActiveSchedules() {
        List<SemesterSchedule> activeSchedules = scheduleService.findActiveSchedules();
        return ResponseEntity.ok(activeSchedules);
    }

    @Operation(summary = "Get semester schedule by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SemesterSchedule> getScheduleById(@PathVariable Long id) {
        SemesterSchedule schedule = scheduleService.findById(id);
        return schedule != null ? ResponseEntity.ok(schedule) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Update semester schedule")
    @PutMapping("/{id}")
    public ResponseEntity<SemesterSchedule> updateSchedule(
            @PathVariable Long id, 
            @Valid @RequestBody SemesterSchedule schedule) {
        try {
            SemesterSchedule updatedSchedule = scheduleService.updateSchedule(id, schedule);
            return ResponseEntity.ok(updatedSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete semester schedule")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Check if score entry is allowed for a class")
    @GetMapping("/check-permission")
    public ResponseEntity<Map<String, Object>> checkScoreEntryPermission(
            @Parameter(description = "Semester (1 or 2)") @RequestParam int semester,
            @Parameter(description = "Year") @RequestParam int year,
            @Parameter(description = "Class name") @RequestParam String className) {
        
        boolean isAllowed = scheduleService.isScoreEntryAllowed(semester, year, className);
        SemesterSchedule activeSchedule = scheduleService.findActiveScheduleForClass(semester, year, className);
        
        Map<String, Object> response = Map.of(
            "isAllowed", isAllowed,
            "semester", semester,
            "year", year,
            "className", className,
            "currentTime", LocalDateTime.now(),
            "activeSchedule", activeSchedule != null ? activeSchedule : "No active schedule found"
        );
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get schedules by year and semester")
    @GetMapping("/by-year-semester")
    public ResponseEntity<List<SemesterSchedule>> getSchedulesByYearAndSemester(
            @Parameter(description = "Year") @RequestParam int year,
            @Parameter(description = "Semester (1 or 2)") @RequestParam int semester) {
        
        List<SemesterSchedule> schedules = scheduleService.findByYearAndSemester(year, semester);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Manually trigger lock of expired schedules")
    @PostMapping("/lock-expired")
    public ResponseEntity<Map<String, String>> lockExpiredSchedules() {
        scheduleService.lockExpiredSchedules();
        return ResponseEntity.ok(Map.of("message", "Expired schedules have been locked"));
    }

    @Operation(summary = "Create a quick schedule for today")
    @PostMapping("/quick-today")
    public ResponseEntity<SemesterSchedule> createTodaySchedule(@RequestBody Map<String, Object> request) {
        try {
            String className = (String) request.get("className");
            Integer semester = (Integer) request.get("semester");
            Integer year = (Integer) request.get("year");
            String scheduleName = (String) request.getOrDefault("scheduleName", 
                "Quick Schedule for " + className + " - Semester " + semester);
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
            
            SemesterSchedule schedule = new SemesterSchedule();
            schedule.setScheduleName(scheduleName);
            schedule.setClassName(className);
            schedule.setSemester(semester);
            schedule.setYear(year);
            schedule.setStartDateTime(now);
            schedule.setEndDateTime(endOfDay);
            schedule.setDescription("Quick schedule created for today");
            schedule.setActive(true);
            
            SemesterSchedule createdSchedule = scheduleService.createSchedule(schedule);
            return ResponseEntity.status(201).body(createdSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}