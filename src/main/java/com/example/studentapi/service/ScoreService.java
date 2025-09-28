package com.example.studentapi.service;

import com.example.studentapi.model.Score;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ScoreService {
    
    // Basic CRUD operations
    Score findById(String id);
    List<Score> findAll();
    Score save(Score score);
    Score update(String id, Score score);
    void delete(String id);
    
    // Batch operations
    List<Score> saveAll(List<Score> scores);
    List<Score> updateAll(List<Score> scores);
    
    // Import/Export operations
    List<Score> importFromExcel(MultipartFile file) throws IOException;
    void exportToExcel(HttpServletResponse response) throws IOException;
    void exportToExcelForTeacher(HttpServletResponse response, Long teacherId) throws IOException;
    
    // Query methods - Basic
    List<Score> findByStudentId(Long studentId);
    List<Score> findByTeacherId(Long teacherId);
    List<Score> findByClassName(String className);
    List<Score> findByYearAndSemester(int year, String semester);
    List<Score> findByClassNameAndYearAndSemester(String className, int year, String semester);
    
    // Query methods - With Subject support
    List<Score> findBySubject(String subject);
    List<Score> findByClassNameAndSubject(String className, String subject);
    List<Score> findByTeacherIdAndSubject(Long teacherId, String subject);
    List<Score> findByStudentIdAndSubject(Long studentId, String subject);
    List<Score> findByClassNameAndSubjectAndYearAndSemester(String className, String subject, int year, String semester);
    
    // Advanced query methods
    List<Score> findByTeacherIdAndClassNameAndSubject(Long teacherId, String className, String subject);
    List<Score> findByStudentIdAndYearAndSemester(Long studentId, int year, String semester);
    List<Score> findByTeacherIdAndYearAndSemester(Long teacherId, int year, String semester);
    
    // Security methods
    boolean teacherHasAccessToScore(Long teacherId, String id);
    boolean teacherHasAccessToClass(Long teacherId, String className, String subject, Integer academicYear, String semester);
    boolean teacherCanCreateScoreForStudent(Long teacherId, Long studentId, String className, String subject, Integer academicYear, String semester);
    
    // Statistical methods
    double getAverageScoreForClass(String className, String subject, int year, String semester);
    long getStudentCountForClass(String className, String subject, int year, String semester);
    List<Score> getTopScoresForClass(String className, String subject, int year, String semester, int limit);
    
    // Validation methods
    boolean validateScoreData(Score score);
    List<String> validateScoreList(List<Score> scores);
}