// Updated ScoreRepository.java
package com.example.studentapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.studentapi.model.Score;
import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    
    // Find scores by class name, year and semester
    List<Score> findByClassNameAndYearAndSemester(String className, int year, int semester);
    
    // Find scores by student ID
    List<Score> findByStudentId(Long studentId);
    
    // Find scores by teacher ID
    List<Score> findByTeacherId(Long teacherId);
    
    // Find scores by class name
    List<Score> findByClassName(String className);
    
    // Find scores by year and semester
    List<Score> findByYearAndSemester(int year, int semester);
    
    // Find scores by student ID and semester
    List<Score> findByStudentIdAndSemester(Long studentId, int semester);
    
    // Find scores by teacher ID and class name (for authorization)
    List<Score> findByTeacherIdAndClassName(Long teacherId, String className);
    
    // Custom query to find scores with student and teacher names
    @Query("SELECT s FROM Score s WHERE s.className = :className AND s.year = :year AND s.semester = :semester ORDER BY s.studentName")
    List<Score> findScoresForClassExport(@Param("className") String className, @Param("year") int year, @Param("semester") int semester);
    
    // Check if teacher has access to specific class
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.teacherId = :teacherId AND s.className = :className")
    boolean teacherHasAccessToClass(@Param("teacherId") Long teacherId, @Param("className") String className);
}