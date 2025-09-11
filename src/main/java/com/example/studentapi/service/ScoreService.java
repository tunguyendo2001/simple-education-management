// Updated ScoreService.java
package com.example.studentapi.service;

import com.example.studentapi.model.Score;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ScoreService {
    Score findById(Long id);
    List<Score> findAll();
    Score save(Score score);
    Score update(Long id, Score score);
    void delete(Long id);
    List<Score> importFromExcel(MultipartFile file) throws IOException;
    void exportToExcel(HttpServletResponse response) throws IOException;
    
    // Add the missing method
    List<Score> findByClassNameAndYearAndSemester(String className, int year, String semester);
    
    // Additional useful methods
    List<Score> findByStudentId(Long studentId);
    List<Score> findByTeacherId(Long teacherId);
    List<Score> findByClassName(String className);
    List<Score> findByYearAndSemester(int year, String semester);
}