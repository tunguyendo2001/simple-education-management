package com.example.studentapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.studentapi.model.Score;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, String> {
    
    // Find scores by student ID
    List<Score> findByStudentId(Long studentId);
    
    // Find scores by teacher ID
    List<Score> findByTeacherId(Long teacherId);
    
    // Find scores by class name
    List<Score> findByClassName(String className);
    
    // Find scores by year and semester
    List<Score> findByYearAndSemester(int year, String semester);
    
    // Find scores by class name, year and semester
    List<Score> findByClassNameAndYearAndSemester(String className, int year, String semester);
    
    // Find scores by student ID and semester
    List<Score> findByStudentIdAndSemester(Long studentId, String semester);
    
    // Find scores by teacher ID and class name (for authorization)
    List<Score> findByTeacherIdAndClassName(Long teacherId, String className);
    
    // ========== SUBJECT-BASED QUERIES ==========
    
    // Find scores by subject
    List<Score> findBySubject(String subject);
    
    // Find scores by class name and subject
    List<Score> findByClassNameAndSubject(String className, String subject);
    
    // Find scores by teacher ID and subject
    List<Score> findByTeacherIdAndSubject(Long teacherId, String subject);
    
    // Find scores by student ID and subject
    List<Score> findByStudentIdAndSubject(Long studentId, String subject);
    
    // ========== COMPLEX QUERIES WITH MULTIPLE PARAMETERS ==========
    
    // Find scores by class name, subject, year and semester
    List<Score> findByClassNameAndSubjectAndYearAndSemester(String className, String subject, int year, String semester);
    
    // Find scores by teacher ID, class name and subject
    List<Score> findByTeacherIdAndClassNameAndSubject(Long teacherId, String className, String subject);
    
    // Find scores by student ID, year and semester
    List<Score> findByStudentIdAndYearAndSemester(Long studentId, int year, String semester);
    
    // Find scores by teacher ID, year and semester
    List<Score> findByTeacherIdAndYearAndSemester(Long teacherId, int year, String semester);
    
    // ========== SECURITY QUERIES ==========
    
    // Find score by custom ID and teacher ID for authorization
    @Query("SELECT s FROM Score s WHERE s.id = :scoreId AND s.teacherId = :teacherId")
    Optional<Score> findByIdAndTeacherId(@Param("scoreId") String scoreId, @Param("teacherId") Long teacherId);
    
    // Check if score exists and belongs to teacher
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.teacherId = :teacherId AND s.id = :scoreId")
    boolean existsByIdAndTeacherId(@Param("scoreId") String scoreId, @Param("teacherId") Long teacherId);
    
    // ========== DUPLICATE CHECK QUERIES ==========
    
    // Find duplicate scores (same student, class, subject, year, semester)
    @Query("SELECT s FROM Score s WHERE s.studentId = :studentId AND s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester")
    List<Score> findDuplicateScore(@Param("studentId") Long studentId, 
                                  @Param("className") String className, 
                                  @Param("subject") String subject, 
                                  @Param("year") int year, 
                                  @Param("semester") String semester);
    
    // Check if duplicate score exists excluding specific ID (for updates)
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.studentId = :studentId AND s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester AND s.id != :excludeId")
    boolean existsDuplicateScore(@Param("studentId") Long studentId, 
                                @Param("className") String className, 
                                @Param("subject") String subject, 
                                @Param("year") int year, 
                                @Param("semester") String semester, 
                                @Param("excludeId") String excludeId);
    
    // ========== STATISTICAL QUERIES ==========
    
    // Get average score for class, subject, year, and semester
    @Query("SELECT AVG(s.tbm) FROM Score s WHERE s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester AND s.tbm IS NOT NULL AND s.tbm > 0")
    Double getAverageScoreForClass(@Param("className") String className, 
                                  @Param("subject") String subject, 
                                  @Param("year") int year, 
                                  @Param("semester") String semester);
    
    // Get student count for class, subject, year, and semester
    @Query("SELECT COUNT(DISTINCT s.studentId) FROM Score s WHERE s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester")
    Long getStudentCountForClass(@Param("className") String className, 
                                @Param("subject") String subject, 
                                @Param("year") int year, 
                                @Param("semester") String semester);
    
    // Get top scores for class, subject, year, and semester (ordered by TBM descending)
    @Query("SELECT s FROM Score s WHERE s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester AND s.tbm IS NOT NULL ORDER BY s.tbm DESC")
    List<Score> findTopScoresForClass(@Param("className") String className, 
                                     @Param("subject") String subject, 
                                     @Param("year") int year, 
                                     @Param("semester") String semester);
    
    // ========== EXPORT QUERIES WITH JOINS ==========
    
    // Custom query to find scores with student and teacher names for export
    @Query("SELECT s FROM Score s WHERE s.className = :className AND s.year = :year AND s.semester = :semester ORDER BY s.studentName")
    List<Score> findScoresForClassExport(@Param("className") String className, 
                                        @Param("year") int year, 
                                        @Param("semester") String semester);
    
    // Find scores for teacher export with proper joins
    @Query("SELECT s FROM Score s LEFT JOIN FETCH s.student LEFT JOIN FETCH s.teacher WHERE s.teacherId = :teacherId ORDER BY s.className, s.subject, s.studentName")
    List<Score> findScoresForTeacherExport(@Param("teacherId") Long teacherId);
    
    // ========== BULK OPERATION SUPPORT ==========
    
    // Find scores for bulk operations by teacher, class, subject, year, and semester
    @Query("SELECT s FROM Score s WHERE s.teacherId = :teacherId AND s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester ORDER BY s.studentName")
    List<Score> findForBulkOperation(@Param("teacherId") Long teacherId,
                                   @Param("className") String className,
                                   @Param("subject") String subject,
                                   @Param("year") int year,
                                   @Param("semester") String semester);
    
    // Find scores by multiple student IDs (for bulk operations)
    @Query("SELECT s FROM Score s WHERE s.studentId IN :studentIds AND s.teacherId = :teacherId AND s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester")
    List<Score> findByStudentIdsAndContext(@Param("studentIds") List<Long> studentIds,
                                          @Param("teacherId") Long teacherId,
                                          @Param("className") String className,
                                          @Param("subject") String subject,
                                          @Param("year") int year,
                                          @Param("semester") String semester);
    
    // ========== CUSTOM ID UTILITY QUERIES ==========
    
    // Check if custom ID exists by components
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.teacherId = :teacherId AND s.studentId = :studentId AND s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester")
    boolean existsByComponents(@Param("teacherId") Long teacherId, 
                              @Param("studentId") Long studentId, 
                              @Param("className") String className, 
                              @Param("subject") String subject, 
                              @Param("year") Integer year, 
                              @Param("semester") String semester);
    
    // Find score by components (alternative to ID lookup)
    @Query("SELECT s FROM Score s WHERE s.teacherId = :teacherId AND s.studentId = :studentId AND s.className = :className AND s.subject = :subject AND s.year = :year AND s.semester = :semester")
    Optional<Score> findByComponents(@Param("teacherId") Long teacherId, 
                                    @Param("studentId") Long studentId, 
                                    @Param("className") String className, 
                                    @Param("subject") String subject, 
                                    @Param("year") Integer year, 
                                    @Param("semester") String semester);
    
    // Find all scores for a teacher-student combination
    @Query("SELECT s FROM Score s WHERE s.teacherId = :teacherId AND s.studentId = :studentId ORDER BY s.year DESC, s.semester DESC")
    List<Score> findByTeacherAndStudent(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);
    
    // ========== METADATA QUERIES ==========
    
    // Find distinct subjects by teacher
    @Query("SELECT DISTINCT s.subject FROM Score s WHERE s.teacherId = :teacherId ORDER BY s.subject")
    List<String> findSubjectsByTeacher(@Param("teacherId") Long teacherId);
    
    // Find distinct subjects by class
    @Query("SELECT DISTINCT s.subject FROM Score s WHERE s.className = :className ORDER BY s.subject")
    List<String> findSubjectsByClass(@Param("className") String className);
    
    // Find distinct classes by teacher and subject
    @Query("SELECT DISTINCT s.className FROM Score s WHERE s.teacherId = :teacherId AND s.subject = :subject ORDER BY s.className")
    List<String> findClassesByTeacherAndSubject(@Param("teacherId") Long teacherId, @Param("subject") String subject);
    
    // Find distinct years by teacher
    @Query("SELECT DISTINCT s.year FROM Score s WHERE s.teacherId = :teacherId ORDER BY s.year DESC")
    List<Integer> findYearsByTeacher(@Param("teacherId") Long teacherId);
    
    // Find distinct semesters by teacher and year
    @Query("SELECT DISTINCT s.semester FROM Score s WHERE s.teacherId = :teacherId AND s.year = :year ORDER BY s.semester")
    List<String> findSemestersByTeacherAndYear(@Param("teacherId") Long teacherId, @Param("year") int year);
    
    // ========== ADVANCED ANALYTICS QUERIES ==========
    
    // Find score trends for a student across semesters
    @Query("SELECT s FROM Score s WHERE s.studentId = :studentId AND s.subject = :subject ORDER BY s.year ASC, s.semester ASC")
    List<Score> findScoreTrendsForStudent(@Param("studentId") Long studentId, @Param("subject") String subject);
    
    // Find class performance comparison across subjects
    @Query("SELECT s.subject, AVG(s.tbm) as avgScore FROM Score s WHERE s.className = :className AND s.year = :year AND s.semester = :semester GROUP BY s.subject ORDER BY avgScore DESC")
    List<Object[]> findClassPerformanceBySubject(@Param("className") String className, 
                                                @Param("year") int year, 
                                                @Param("semester") String semester);
    
    // Find teacher performance across classes for a subject
    @Query("SELECT s.className, AVG(s.tbm) as avgScore, COUNT(s) as studentCount FROM Score s WHERE s.teacherId = :teacherId AND s.subject = :subject AND s.year = :year AND s.semester = :semester GROUP BY s.className ORDER BY avgScore DESC")
    List<Object[]> findTeacherPerformanceAcrossClasses(@Param("teacherId") Long teacherId, 
                                                      @Param("subject") String subject,
                                                      @Param("year") int year, 
                                                      @Param("semester") String semester);
    
    // ========== VALIDATION SUPPORT QUERIES ==========
    
    // Check if student exists in the system (through scores)
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.studentId = :studentId")
    boolean existsStudentInScores(@Param("studentId") Long studentId);
    
    // Check if teacher exists in the system (through scores)
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.teacherId = :teacherId")
    boolean existsTeacherInScores(@Param("teacherId") Long teacherId);
    
    // Check if class exists in the system (through scores)
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.className = :className")
    boolean existsClassInScores(@Param("className") String className);
    
    // ========== CLEANUP AND MAINTENANCE QUERIES ==========
    
    // Find orphaned scores (without corresponding student/teacher records)
    @Query("SELECT s FROM Score s WHERE s.studentId NOT IN (SELECT st.id FROM Student st) OR s.teacherId NOT IN (SELECT t.id FROM Teacher t)")
    List<Score> findOrphanedScores();
    
    // Find scores with null or invalid TBM values
    @Query("SELECT s FROM Score s WHERE s.tbm IS NULL OR s.tbm < 0 OR s.tbm > 10")
    List<Score> findScoresWithInvalidTbm();
    
    // Find scores missing student or teacher names
    @Query("SELECT s FROM Score s WHERE s.studentName IS NULL OR s.studentName = '' OR s.teacherName IS NULL OR s.teacherName = ''")
    List<Score> findScoresWithMissingNames();
}