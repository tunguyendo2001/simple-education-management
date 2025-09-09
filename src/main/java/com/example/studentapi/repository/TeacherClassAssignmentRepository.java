package com.example.studentapi.repository;

import com.example.studentapi.model.TeacherClassAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeacherClassAssignmentRepository extends JpaRepository<TeacherClassAssignment, Long> {
    
    List<TeacherClassAssignment> findByTeacherIdAndIsActiveTrue(Long teacherId);

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

    @Query("SELECT tca FROM TeacherClassAssignment tca JOIN tca.schoolClass c WHERE tca.teacherId = :teacherId AND c.className = :className AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndClassNameAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId, 
        @Param("className") String className, 
        @Param("academicYear") int academicYear, 
        @Param("semester") String semester
    );
    
    // Use schoolClassId instead of classId
    @Query("SELECT tca FROM TeacherClassAssignment tca WHERE tca.teacherId = :teacherId AND tca.schoolClassId = :schoolClassId")
    List<TeacherClassAssignment> findByTeacherIdAndSchoolClassIdQuery(@Param("teacherId") Long teacherId, @Param("schoolClassId") Long schoolClassId);
    
    @Query("SELECT tca FROM TeacherClassAssignment tca WHERE tca.teacherId = :teacherId AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId, 
        @Param("academicYear") int academicYear, 
        @Param("semester") String semester
    );
    
    @Query("SELECT tca FROM TeacherClassAssignment tca JOIN tca.schoolClass c WHERE tca.teacherId = :teacherId AND c.className = :className AND tca.subject = :subject AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndClassNameAndSubjectAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId,
        @Param("className") String className,
        @Param("subject") String subject,
        @Param("academicYear") int academicYear,
        @Param("semester") String semester
    );

    @Query("SELECT tca FROM TeacherClassAssignment tca JOIN tca.schoolClass c WHERE tca.teacherId = :teacherId AND tca.subject = :subject AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndSubjectAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId,
        @Param("subject") String subject,
        @Param("academicYear") int academicYear,
        @Param("semester") String semester
    );
    
    @Query("SELECT COUNT(sca) > 0 FROM StudentClassAssignment sca WHERE sca.student.id = :studentId AND sca.classEntity.id = :schoolClassId AND sca.academicYear = :academicYear AND (sca.semester = :semester OR sca.semester = 'BOTH') AND sca.isActive = true")
    boolean isStudentInTeacherClass(
        @Param("studentId") Long studentId, 
        @Param("schoolClassId") Long schoolClassId, 
        @Param("academicYear") int academicYear, 
        @Param("semester") String semester
    );
}