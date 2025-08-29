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
}