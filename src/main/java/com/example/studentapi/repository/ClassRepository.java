package com.example.studentapi.repository;

import com.example.studentapi.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByClassName(String className);
    List<Class> findByGradeAndAcademicYear(int grade, int academicYear);
    List<Class> findByAcademicYearAndIsActiveTrue(int academicYear);
    
    @Query("SELECT c FROM Class c WHERE c.academicYear = :year AND c.semester = :semester AND c.isActive = true")
    List<Class> findByAcademicYearAndSemester(@Param("year") int year, @Param("semester") String semester);
}