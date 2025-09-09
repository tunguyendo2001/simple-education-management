package com.example.studentapi.service.impl;

import com.example.studentapi.model.SchoolClass;
import com.example.studentapi.model.Student;
import com.example.studentapi.model.StudentClassAssignment;
import com.example.studentapi.model.Teacher;
import com.example.studentapi.model.TeacherClassAssignment;
import com.example.studentapi.repository.ClassRepository;
import com.example.studentapi.repository.StudentClassAssignmentRepository;
import com.example.studentapi.repository.StudentRepository;
import com.example.studentapi.repository.TeacherClassAssignmentRepository;
import com.example.studentapi.repository.TeacherRepository;
import com.example.studentapi.service.SchoolClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolClassServiceImpl implements SchoolClassService {

    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherClassAssignmentRepository teacherAssignmentRepository;

    @Autowired
    private StudentClassAssignmentRepository studentAssignmentRepository;

    @Override
    public SchoolClass createClass(SchoolClass classEntity) {
        // Check if class name already exists
        if (classRepository.findByClassName(classEntity.getClassName()).isPresent()) {
            throw new IllegalArgumentException("Class with name " + classEntity.getClassName() + " already exists");
        }
        return classRepository.save(classEntity);
    }

    @Override
    public SchoolClass findById(Long id) {
        return classRepository.findById(id).orElse(null);
    }

    @Override
    public SchoolClass findByClassName(String className) {
        return classRepository.findByClassName(className).orElse(null);
    }

    @Override
    public List<SchoolClass> findAllActive() {
        return classRepository.findAll().stream()
            .filter(SchoolClass::isActive)
            .collect(Collectors.toList());
    }

    @Override
    public List<SchoolClass> findByAcademicYearAndSemester(int academicYear, String semester) {
        return classRepository.findByAcademicYearAndSemester(academicYear, semester);
    }

    @Override
    public TeacherClassAssignment assignTeacherToClass(Long teacherId, Long classId, String subject, 
                                                      int academicYear, String semester, String role, 
                                                      boolean isPrimary, String assignedBy) {
        
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        
        SchoolClass classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new IllegalArgumentException("Class not found with id: " + classId));

        // Check if assignment already exists - using the corrected method
        List<TeacherClassAssignment> existingAssignments = teacherAssignmentRepository
            .findByTeacherIdAndSubjectAndAcademicYearAndSemester(
                teacherId, subject, academicYear, semester);
        
        // Filter by class name since we can't easily join in the repository method
        existingAssignments = existingAssignments.stream()
            .filter(assignment -> assignment.getClassEntity().getClassName().equals(classEntity.getClassName()))
            .collect(Collectors.toList());
        
        if (!existingAssignments.isEmpty()) {
            throw new IllegalArgumentException("Teacher is already assigned to this class for this subject");
        }

        TeacherClassAssignment assignment = new TeacherClassAssignment();
        assignment.setTeacher(teacher);
        assignment.setClassEntity(classEntity);
        assignment.setSubject(subject);
        assignment.setAcademicYear(academicYear);
        assignment.setSemester(semester);
        assignment.setRole(TeacherClassAssignment.AssignmentRole.valueOf(role));
        assignment.setPrimaryTeacher(isPrimary);
        assignment.setAssignedBy(assignedBy);
        assignment.setActive(true);

        return teacherAssignmentRepository.save(assignment);
    }

    @Override
    public StudentClassAssignment assignStudentToClass(Long studentId, Long classId, int academicYear, 
                                                      String semester, String studentNumber, String enrolledBy) {
        
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
        
        SchoolClass classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new IllegalArgumentException("Class not found with id: " + classId));

        // Check if student is already assigned to this class
        List<StudentClassAssignment> existingAssignments = studentAssignmentRepository
            .findByStudentIdAndClassNameAndAcademicYearAndSemester(
                studentId, classEntity.getClassName(), academicYear, semester);
        
        if (!existingAssignments.isEmpty()) {
            throw new IllegalArgumentException("Student is already assigned to this class");
        }

        StudentClassAssignment assignment = new StudentClassAssignment();
        assignment.setStudent(student);
        assignment.setClassEntity(classEntity);
        assignment.setAcademicYear(academicYear);
        assignment.setSemester(semester);
        assignment.setStudentNumber(studentNumber);
        assignment.setEnrolledBy(enrolledBy);
        assignment.setActive(true);

        return studentAssignmentRepository.save(assignment);
    }

    @Override
    public List<StudentClassAssignment> getStudentsInClass(Long classId, int academicYear, String semester) {
        return studentAssignmentRepository.findStudentsInClass(classId, academicYear, semester);
    }

    @Override
    public boolean teacherHasAccessToClass(Long teacherId, Long schoolClassId) {
        return teacherAssignmentRepository.existsByTeacherIdAndSchoolClassIdAndIsActive(teacherId, schoolClassId);
    }

    @Override
    public List<TeacherClassAssignment> getTeacherAssignments(Long teacherId) {
        return teacherAssignmentRepository.findActiveAssignmentsByTeacherId(teacherId);
    }

    @Override
    public List<TeacherClassAssignment> getClassAssignments(Long schoolClassId) {
        return teacherAssignmentRepository.findActiveAssignmentsBySchoolClassId(schoolClassId);
    }

    @Override
    public List<TeacherClassAssignment> getTeachersInClass(Long classId, int academicYear, String semester) {
        return teacherAssignmentRepository.findActiveAssignmentsBySchoolClassId(classId)
            .stream()
            .filter(assignment -> assignment.getAcademicYear() == academicYear)
            .filter(assignment -> assignment.getSemester().equals(semester) || assignment.getSemester().equals("BOTH"))
            .filter(TeacherClassAssignment::isActive)
            .collect(Collectors.toList());
    }

    @Override
    public void removeTeacherAssignment(Long assignmentId, Long requestingTeacherId) {
        TeacherClassAssignment assignment = teacherAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        // Only allow removal by the same teacher or admin logic can be added here
        if (!assignment.getTeacher().getId().equals(requestingTeacherId)) {
            throw new IllegalArgumentException("Not authorized to remove this assignment");
        }
        
        assignment.setActive(false);
        teacherAssignmentRepository.save(assignment);
    }

    @Override
    public void removeStudentAssignment(Long assignmentId, Long requestingTeacherId) {
        StudentClassAssignment assignment = studentAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        // Check if requesting teacher has authority over this class - using corrected method
        List<TeacherClassAssignment> teacherAssignments = teacherAssignmentRepository
            .findByTeacherIdAndSchoolClassId(requestingTeacherId, assignment.getClassEntity().getId());
        
        if (teacherAssignments.isEmpty() || teacherAssignments.stream().noneMatch(TeacherClassAssignment::isActive)) {
            throw new IllegalArgumentException("Not authorized to remove student from this class");
        }
        
        assignment.setActive(false);
        studentAssignmentRepository.save(assignment);
    }

    @Override
    public List<StudentClassAssignment> bulkAssignStudentsToClass(List<Long> studentIds, Long classId, 
                                                                 int academicYear, String semester, String enrolledBy) {
        List<StudentClassAssignment> assignments = new ArrayList<>();
        
        for (Long studentId : studentIds) {
            try {
                StudentClassAssignment assignment = assignStudentToClass(
                    studentId, classId, academicYear, semester, "", enrolledBy);
                assignments.add(assignment);
            } catch (IllegalArgumentException e) {
                // Log the error but continue with other students
                System.err.println("Failed to assign student " + studentId + ": " + e.getMessage());
            }
        }
        
        return assignments;
    }
}