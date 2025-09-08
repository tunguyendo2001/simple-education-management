package com.example.studentapi.repository;

import com.example.studentapi.model.TeacherClassAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeacherClassAssignmentRepository extends JpaRepository<TeacherClassAssignment, Long> {
    
    // Fixed method name to use schoolClassId instead of classId
    List<TeacherClassAssignment> findByTeacherIdAndSchoolClassId(Long teacherId, Long schoolClassId);
    
    // Find active assignments by teacher ID
    List<TeacherClassAssignment> findByTeacherIdAndIsActive(Long teacherId, Boolean isActive);
    
    // Find active assignments by school class ID
    List<TeacherClassAssignment> findBySchoolClassIdAndIsActive(Long schoolClassId, Boolean isActive);
    
    // Check if teacher has access to class (using custom query to be explicit)
    @Query("SELECT COUNT(tca) > 0 FROM TeacherClassAssignment tca WHERE tca.teacherId = :teacherId AND tca.schoolClassId = :schoolClassId AND tca.isActive = true")
    boolean existsByTeacherIdAndSchoolClassIdAndIsActive(@Param("teacherId") Long teacherId, @Param("schoolClassId") Long schoolClassId);
    
    // Get all classes for a teacher
    @Query("SELECT tca FROM TeacherClassAssignment tca WHERE tca.teacherId = :teacherId AND tca.isActive = true")
    List<TeacherClassAssignment> findActiveAssignmentsByTeacherId(@Param("teacherId") Long teacherId);
    
    // Get all teachers for a class
    @Query("SELECT tca FROM TeacherClassAssignment tca WHERE tca.schoolClassId = :schoolClassId AND tca.isActive = true")
    List<TeacherClassAssignment> findActiveAssignmentsBySchoolClassId(@Param("schoolClassId") Long schoolClassId);
}