package com.example.studentapi.service.impl;

import com.example.studentapi.model.Class;
import com.example.studentapi.model.Student;
import com.example.studentapi.model.StudentClassAssignment;
import com.example.studentapi.model.Teacher;
import com.example.studentapi.model.TeacherClassAssignment;
import com.example.studentapi.repository.ClassRepository;
import com.example.studentapi.repository.StudentClassAssignmentRepository;
import com.example.studentapi.repository.StudentRepository;
import com.example.studentapi.repository.TeacherClassAssignmentRepository;
import com.example.studentapi.repository.TeacherRepository;
import com.example.studentapi.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ClassServiceImpl implements ClassService {

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
    public Class createClass(Class classEntity) {
        // Check if class name already exists
        if (classRepository.findByClassName(classEntity.getClassName()).isPresent()) {
            throw new IllegalArgumentException("Class with name " + classEntity.getClassName() + " already exists");
        }
        return classRepository.save(classEntity);
    }

    @Override
    public Class findById(Long id) {
        return classRepository.findById(id).orElse(null);
    }

    @Override
    public Class findByClassName(String className) {
        return classRepository.findByClassName(className).orElse(null);
    }

    @Override
    public List<Class> findAllActive() {
        return classRepository.findAll().stream()
            .filter(Class::isActive)
            .toList();
    }

    @Override
    public List<Class> findByAcademicYearAndSemester(int academicYear, String semester) {
        return classRepository.findByAcademicYearAndSemester(academicYear, semester);
    }

    @Override
    public TeacherClassAssignment assignTeacherToClass(Long teacherId, Long classId, String subject, 
                                                      int academicYear, String semester, String role, 
                                                      boolean isPrimary, String assignedBy) {
        
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        
        Class classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new IllegalArgumentException("Class not found with id: " + classId));

        // Check if assignment already exists
        List<TeacherClassAssignment> existingAssignments = teacherAssignmentRepository
            .findByTeacherIdAndClassNameAndSubjectAndAcademicYearAndSemester(
                teacherId, classEntity.getClassName(), subject, academicYear, semester);
        
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
        
        Class classEntity = classRepository.findById(classId)
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
    public List<TeacherClassAssignment> getTeachersInClass(Long classId, int academicYear, String semester) {
        // This method would need a corresponding repository method
        return teacherAssignmentRepository.findByTeacherIdAndClassId(null, classId)
            .stream()
            .filter(assignment -> assignment.getAcademicYear() == academicYear)
            .filter(assignment -> assignment.getSemester().equals(semester) || assignment.getSemester().equals("BOTH"))
            .filter(TeacherClassAssignment::isActive)
            .toList();
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
        
        // Check if requesting teacher has authority over this class
        List<TeacherClassAssignment> teacherAssignments = teacherAssignmentRepository
            .findByTeacherIdAndClassId(requestingTeacherId, assignment.getClassEntity().getId());
        
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
