package com.example.studentapi.controller;

import com.example.studentapi.model.SchoolClass;
import com.example.studentapi.model.TeacherClassAssignment;
import com.example.studentapi.model.StudentClassAssignment;
import com.example.studentapi.service.SchoolClassService;
import com.example.studentapi.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Class Management", description = "APIs for managing classes and assignments")
public class ClassController {

    @Autowired
    private SchoolClassService classService;
    
    @Autowired
    private AuthorizationService authorizationService;

    @Operation(summary = "Create a new class")
    @PostMapping
    public ResponseEntity<SchoolClass> createClass(@Valid @RequestBody SchoolClass classEntity) {
        SchoolClass createdClass = classService.createClass(classEntity);
        return ResponseEntity.status(201).body(createdClass);
    }

    @Operation(summary = "Get all classes")
    @GetMapping
    public ResponseEntity<List<SchoolClass>> getAllClasses() {
        List<SchoolClass> classes = classService.findAllActive();
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Get class by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SchoolClass> getClassById(@PathVariable Long id) {
        SchoolClass classEntity = classService.findById(id);
        return classEntity != null ? ResponseEntity.ok(classEntity) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Assign teacher to class")
    @PostMapping("/assign-teacher")
    public ResponseEntity<?> assignTeacher(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = Long.valueOf(request.get("teacherId").toString());
            Long classId = Long.valueOf(request.get("classId").toString());
            String subject = request.get("subject").toString();
            int academicYear = Integer.parseInt(request.get("academicYear").toString());
            String semester = request.get("semester").toString();
            String role = request.getOrDefault("role", "SUBJECT_TEACHER").toString();
            boolean isPrimary = Boolean.parseBoolean(request.getOrDefault("isPrimary", "false").toString());
            String assignedBy = request.getOrDefault("assignedBy", "Admin").toString();

            TeacherClassAssignment assignment = classService.assignTeacherToClass(
                teacherId, classId, subject, academicYear, semester, role, isPrimary, assignedBy);
            
            return ResponseEntity.status(201).body(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Assign student to class")
    @PostMapping("/assign-student")
    public ResponseEntity<?> assignStudent(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            Long classId = Long.valueOf(request.get("classId").toString());
            int academicYear = Integer.parseInt(request.get("academicYear").toString());
            String semester = request.get("semester").toString();
            String studentNumber = request.getOrDefault("studentNumber", "").toString();
            String enrolledBy = request.getOrDefault("enrolledBy", "Admin").toString();

            StudentClassAssignment assignment = classService.assignStudentToClass(
                studentId, classId, academicYear, semester, studentNumber, enrolledBy);
            
            return ResponseEntity.status(201).body(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get teacher's assigned classes")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TeacherClassAssignment>> getTeacherClasses(@PathVariable Long teacherId) {
        List<TeacherClassAssignment> assignments = authorizationService.getTeacherClassAssignments(teacherId);
        return ResponseEntity.ok(assignments);
    }

    @Operation(summary = "Get students in a class")
    @GetMapping("/{classId}/students")
    public ResponseEntity<?> getStudentsInClass(
            @PathVariable Long classId,
            @RequestParam int academicYear,
            @RequestParam String semester,
            @RequestParam Long teacherId) {
        
        // Check if teacher has access to this class
        if (!authorizationService.canTeacherAccessClass(teacherId, classId)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access denied",
                "message", "Teacher is not authorized to view students in this class"
            ));
        }
        
        List<StudentClassAssignment> students = classService.getStudentsInClass(classId, academicYear, semester);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Get classes by academic year and semester")
    @GetMapping("/by-year-semester")
    public ResponseEntity<List<SchoolClass>> getClassesByYearAndSemester(
            @RequestParam int academicYear,
            @RequestParam String semester) {
        
        List<SchoolClass> classes = classService.findByAcademicYearAndSemester(academicYear, semester);
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Remove teacher from class")
    @DeleteMapping("/teacher-assignment/{assignmentId}")
    public ResponseEntity<?> removeTeacherAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long requestingTeacherId) {
        
        try {
            classService.removeTeacherAssignment(assignmentId, requestingTeacherId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Remove student from class")
    @DeleteMapping("/student-assignment/{assignmentId}")
    public ResponseEntity<?> removeStudentAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long requestingTeacherId) {
        
        try {
            classService.removeStudentAssignment(assignmentId, requestingTeacherId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Bulk assign students to class")
    @PostMapping("/{classId}/bulk-assign-students")
    public ResponseEntity<?> bulkAssignStudents(
            @PathVariable Long classId,
            @RequestBody Map<String, Object> request) {
        
        try {
            @SuppressWarnings("unchecked")
            List<Long> studentIds = (List<Long>) request.get("studentIds");
            int academicYear = Integer.parseInt(request.get("academicYear").toString());
            String semester = request.get("semester").toString();
            String enrolledBy = request.getOrDefault("enrolledBy", "Admin").toString();
            
            List<StudentClassAssignment> assignments = classService.bulkAssignStudentsToClass(
                studentIds, classId, academicYear, semester, enrolledBy);
            
            return ResponseEntity.status(201).body(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}