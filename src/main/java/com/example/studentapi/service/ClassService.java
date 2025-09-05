package com.example.studentapi.service;

import com.example.studentapi.model.Class;
import com.example.studentapi.model.StudentClassAssignment;
import com.example.studentapi.model.TeacherClassAssignment;

import java.util.List;

public interface ClassService {
    Class createClass(Class classEntity);
    Class findById(Long id);
    Class findByClassName(String className);
    List<Class> findAllActive();
    List<Class> findByAcademicYearAndSemester(int academicYear, String semester);
    
    TeacherClassAssignment assignTeacherToClass(Long teacherId, Long classId, String subject, 
                                               int academicYear, String semester, String role, 
                                               boolean isPrimary, String assignedBy);
    
    StudentClassAssignment assignStudentToClass(Long studentId, Long classId, int academicYear, 
                                               String semester, String studentNumber, String enrolledBy);
    
    List<StudentClassAssignment> getStudentsInClass(Long classId, int academicYear, String semester);
    List<TeacherClassAssignment> getTeachersInClass(Long classId, int academicYear, String semester);
    
    void removeTeacherAssignment(Long assignmentId, Long requestingTeacherId);
    void removeStudentAssignment(Long assignmentId, Long requestingTeacherId);
    
    List<StudentClassAssignment> bulkAssignStudentsToClass(List<Long> studentIds, Long classId, 
                                                          int academicYear, String semester, String enrolledBy);
}