package com.example.studentapi.service;

import com.example.studentapi.model.SchoolClass;
import com.example.studentapi.model.StudentClassAssignment;
import com.example.studentapi.model.TeacherClassAssignment;
import java.util.List;

public interface SchoolClassService {
    
    /**
     * Check if a teacher has access to a specific class
     * @param teacherId The teacher's ID
     * @param schoolClassId The school class ID
     * @return true if teacher has access, false otherwise
     */
    boolean teacherHasAccessToClass(Long teacherId, Long schoolClassId);
    
    /**
     * Get all active class assignments for a teacher
     * @param teacherId The teacher's ID
     * @return List of teacher class assignments
     */
    List<TeacherClassAssignment> getTeacherAssignments(Long teacherId);
    
    /**
     * Get all active teacher assignments for a class
     * @param schoolClassId The school class ID
     * @return List of teacher class assignments
     */
    List<TeacherClassAssignment> getClassAssignments(Long schoolClassId);

    SchoolClass createClass(SchoolClass classEntity);
    SchoolClass findById(Long id);
    SchoolClass findByClassName(String className);
    List<SchoolClass> findAllActive();
    List<SchoolClass> findByAcademicYearAndSemester(int academicYear, String semester);
    
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