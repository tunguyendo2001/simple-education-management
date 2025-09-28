package com.example.studentapi.service;

import com.example.studentapi.model.TeacherClassAssignment;
import java.util.List;

public interface AuthorizationService {
    boolean canTeacherAccessClass(Long teacherId, String className, int academicYear, String semester);
    boolean canTeacherAccessClass(Long teacherId, Long classId);
    boolean canTeacherAccessStudent(Long teacherId, Long studentId, int academicYear, String semester);
    boolean canTeacherModifyScore(Long teacherId, String scoreId);
    List<String> getTeacherAccessibleClasses(Long teacherId, int academicYear, String semester);
    List<TeacherClassAssignment> getTeacherClassAssignments(Long teacherId);
    boolean isTeacherAuthorizedForSubject(Long teacherId, String className, String subject, int academicYear, String semester);
}
