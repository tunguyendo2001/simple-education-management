package com.example.studentapi.repository;

import com.example.studentapi.model.SemesterSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterScheduleRepository extends JpaRepository<SemesterSchedule, Long> {
    
    // Find active schedule for specific semester, year, and class
    @Query("SELECT s FROM SemesterSchedule s WHERE s.semester = :semester AND s.year = :year AND s.className = :className AND s.isActive = true")
    Optional<SemesterSchedule> findActiveSchedule(@Param("semester") int semester, 
                                                  @Param("year") int year, 
                                                  @Param("className") String className);
    
    // Find all schedules that should be locked (past end time but not locked yet)
    @Query("SELECT s FROM SemesterSchedule s WHERE s.endDateTime < :currentTime AND s.isLocked = false AND s.isActive = true")
    List<SemesterSchedule> findSchedulesToLock(@Param("currentTime") LocalDateTime currentTime);
    
    // Find all active schedules
    List<SemesterSchedule> findByIsActiveTrue();
    
    // Find schedules by year and semester
    List<SemesterSchedule> findByYearAndSemesterOrderByClassNameAsc(int year, int semester);
    
    // Find schedules by class name
    List<SemesterSchedule> findByClassNameOrderByYearDescSemesterDesc(String className);
    
    // Check if there's an active schedule for a specific period
    @Query("SELECT COUNT(s) > 0 FROM SemesterSchedule s WHERE s.semester = :semester AND s.year = :year AND s.className = :className AND s.isActive = true AND ((s.startDateTime <= :endTime AND s.endDateTime >= :startTime))")
    boolean hasOverlappingSchedule(@Param("semester") int semester, 
                                   @Param("year") int year, 
                                   @Param("className") String className,
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
}