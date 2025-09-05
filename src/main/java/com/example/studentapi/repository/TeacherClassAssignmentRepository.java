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
    
    @Query("SELECT tca FROM TeacherClassAssignment tca JOIN tca.classEntity c WHERE tca.teacher.id = :teacherId AND c.className = :className AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndClassNameAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId, 
        @Param("className") String className, 
        @Param("academicYear") int academicYear, 
        @Param("semester") String semester
    );
    
    List<TeacherClassAssignment> findByTeacherIdAndClassId(Long teacherId, Long classId);
    
    @Query("SELECT tca FROM TeacherClassAssignment tca WHERE tca.teacher.id = :teacherId AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId, 
        @Param("academicYear") int academicYear, 
        @Param("semester") String semester
    );
    
    @Query("SELECT tca FROM TeacherClassAssignment tca JOIN tca.classEntity c WHERE tca.teacher.id = :teacherId AND c.className = :className AND tca.subject = :subject AND tca.academicYear = :academicYear AND (tca.semester = :semester OR tca.semester = 'BOTH') AND tca.isActive = true")
    List<TeacherClassAssignment> findByTeacherIdAndClassNameAndSubjectAndAcademicYearAndSemester(
        @Param("teacherId") Long teacherId,
        @Param("className") String className,
        @Param("subject") String subject,
        @Param("academicYear") int academicYear,
        @Param("semester") String semester
    );
    
    @Query("SELECT COUNT(sca) > 0 FROM StudentClassAssignment sca WHERE sca.student.id = :studentId AND sca.classEntity.id = :classId AND sca.academicYear = :academicYear AND (sca.semester = :semester OR sca.semester = 'BOTH') AND sca.isActive = true")
    boolean isStudentInTeacherClass(
        @Param("studentId") Long studentId, 
        @Param("classId") Long classId, 
        @Param("academicYear") int academicYear, 
        @Param("semester") String semester
    );
}