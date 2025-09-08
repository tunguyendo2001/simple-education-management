package com.example.studentapi.service;

import com.example.studentapi.model.TeacherClassAssignment;
import java.util.List;

public interface ClassService {
    
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
}