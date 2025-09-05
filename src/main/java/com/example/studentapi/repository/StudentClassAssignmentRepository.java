package com.example.studentapi.repository;

import com.example.studentapi.model.StudentClassAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentClassAssignmentRepository extends JpaRepository<StudentClassAssignment, Long> {
    
    List<StudentClassAssignment> findByStudentIdAndIsActiveTrue(Long studentId);
    List<StudentClassAssignment> findByClassIdAndIsActiveTrue(Long classId);
    
    @Query("SELECT sca FROM StudentClassAssignment sca JOIN sca.classEntity c WHERE sca.student.id = :studentId AND c.className = :className AND sca.academicYear = :academicYear AND (sca.semester = :semester OR sca.semester = 'BOTH') AND sca.isActive = true")
    List<StudentClassAssignment> findByStudentIdAndClassNameAndAcademicYearAndSemester(
        @Param("studentId") Long studentId,
        @Param("className") String className,
        @Param("academicYear") int academicYear,
        @Param("semester") String semester
    );
    
    @Query("SELECT sca FROM StudentClassAssignment sca WHERE sca.classEntity.id = :classId AND sca.academicYear = :academicYear AND (sca.semester = :semester OR sca.semester = 'BOTH') AND sca.isActive = true")
    List<StudentClassAssignment> findStudentsInClass(
        @Param("classId") Long classId,
        @Param("academicYear") int academicYear,
        @Param("semester") String semester
    );
}