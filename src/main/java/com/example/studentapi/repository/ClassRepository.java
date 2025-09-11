package com.example.studentapi.repository;

import com.example.studentapi.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<SchoolClass, Long> {
    Optional<SchoolClass> findByClassName(String className);
    Optional<SchoolClass> findByName(String name);
    List<SchoolClass> findByGradeLevelAndAcademicYear(int gradeLevel, int academicYear);
    List<SchoolClass> findByAcademicYearAndIsActiveTrue(int academicYear);
    
    @Query("SELECT c FROM SchoolClass c WHERE c.academicYear = :year AND c.semester = :semester AND c.isActive = true")
    List<SchoolClass> findByAcademicYearAndSemester(@Param("year") int year, @Param("semester") String semester);
    
    @Query("SELECT c FROM SchoolClass c WHERE c.academicYear = :year AND c.semester = :semester AND c.isActive = true")
    List<SchoolClass> findByAcademicYearAndSemesterInt(@Param("year") int year, @Param("semester") String semester);
}