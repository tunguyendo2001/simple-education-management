package com.example.studentapi.service.impl;

import com.example.studentapi.model.Score;
import com.example.studentapi.model.TeacherClassAssignment;
import com.example.studentapi.repository.ScoreRepository;
import com.example.studentapi.repository.TeacherClassAssignmentRepository;
import com.example.studentapi.service.AuthorizationService;
import com.example.studentapi.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    private TeacherClassAssignmentRepository assignmentRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public boolean canTeacherAccessClass(Long teacherId, String className, int academicYear, String semester) {
        List<TeacherClassAssignment> assignments = assignmentRepository
            .findByTeacherIdAndClassNameAndAcademicYearAndSemester(teacherId, className, academicYear, semester);
        
        return assignments.stream().anyMatch(assignment -> 
            assignment.isActive() && 
            (assignment.getSemester().equals(semester) || assignment.getSemester().equals("BOTH"))
        );
    }

    @Override
    public boolean canTeacherAccessClass(Long teacherId, Long classId) {
        List<TeacherClassAssignment> assignments = assignmentRepository
            .findByTeacherIdAndClassId(teacherId, classId);
        
        return assignments.stream().anyMatch(assignment -> assignment.isActive());
    }

    @Override
    public boolean canTeacherAccessStudent(Long teacherId, Long studentId, int academicYear, String semester) {
        // Get all classes that this teacher teaches
        List<TeacherClassAssignment> teacherAssignments = assignmentRepository
            .findByTeacherIdAndAcademicYearAndSemester(teacherId, academicYear, semester);
        
        // Check if any of the teacher's classes contain this student
        return teacherAssignments.stream()
            .filter(TeacherClassAssignment::isActive)
            .anyMatch(assignment -> 
                assignmentRepository.isStudentInTeacherClass(studentId, assignment.getClassEntity().getId(), academicYear, semester)
            );
    }

    @Override
    public boolean canTeacherModifyScore(Long teacherId, Long scoreId) {
        Score score = scoreRepository.findById(scoreId).orElse(null);
        if (score == null) {
            return false;
        }
        
        return canTeacherAccessClass(teacherId, score.getClassName(), score.getYear(), 
            String.valueOf(score.getSemester()));
    }

    @Override
    public List<String> getTeacherAccessibleClasses(Long teacherId, int academicYear, String semester) {
        List<TeacherClassAssignment> assignments = assignmentRepository
            .findByTeacherIdAndAcademicYearAndSemester(teacherId, academicYear, semester);
        
        return assignments.stream()
            .filter(TeacherClassAssignment::isActive)
            .map(assignment -> assignment.getClassEntity().getClassName())
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public List<TeacherClassAssignment> getTeacherClassAssignments(Long teacherId) {
        return assignmentRepository.findByTeacherIdAndIsActiveTrue(teacherId);
    }

    @Override
    public boolean isTeacherAuthorizedForSubject(Long teacherId, String className, String subject, 
                                                int academicYear, String semester) {
        List<TeacherClassAssignment> assignments = assignmentRepository
            .findByTeacherIdAndClassNameAndSubjectAndAcademicYearAndSemester(
                teacherId, className, subject, academicYear, semester);
        
        return assignments.stream().anyMatch(assignment -> 
            assignment.isActive() && 
            (assignment.getSemester().equals(semester) || assignment.getSemester().equals("BOTH"))
        );
    }
}