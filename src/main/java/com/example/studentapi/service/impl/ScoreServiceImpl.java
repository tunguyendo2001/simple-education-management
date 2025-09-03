package com.example.studentapi.service.impl;

import com.example.studentapi.model.Score;
import com.example.studentapi.repository.ScoreRepository;
import com.example.studentapi.service.ScoreService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public Score findById(Long id) {
        return scoreRepository.findById(id).orElse(null);
    }

    @Override
    public List<Score> findAll() {
        return scoreRepository.findAll();
    }

    @Override
    public Score save(Score score) {
        return scoreRepository.save(score);
    }

    @Override
    public Score update(Long id, Score score) {
        if (scoreRepository.existsById(id)) {
            score.setId(id);
            return scoreRepository.save(score);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        scoreRepository.deleteById(id);
    }

    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        // Query scores table in MySQL
        List<Score> scores = scoreRepository.findAll();
        if (scores.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No score data available to export");
            return;
        }

        // Create an Excel workbook
        Workbook workbook = new XSSFWorkbook();
        try {
            // Group scores by className
            Map<String, List<Score>> classMap = scores.stream()
                    .collect(Collectors.groupingBy(Score::getClassName));

            for (Map.Entry<String, List<Score>> classEntry : classMap.entrySet()) {
                String className = classEntry.getKey();
                List<Score> classScores = classEntry.getValue();
                if (classScores.isEmpty()) {
                    continue;
                }

                // Create a sheet
                Sheet sheet = workbook.createSheet(className + "-Tin học");

                // Get teacher name (assume consistent for class)
                String teacherName = classScores.get(0).getTeacherName() != null ? classScores.get(0).getTeacherName() : "";

                // Group by studentId to get unique students
                Map<Long, List<Score>> studentMap = classScores.stream()
                        .collect(Collectors.groupingBy(Score::getStudentId));

                int ss = studentMap.size();

                // Sort students by name
                List<Long> sortedStudentIds = new ArrayList<>(studentMap.keySet());
                sortedStudentIds.sort((id1, id2) -> studentMap.get(id1).get(0).getStudentName()
                        .compareTo(studentMap.get(id2).get(0).getStudentName()));

                // Create header row 0
                Row row0 = sheet.createRow(0);
                int col = 0;
                row0.createCell(col++).setCellValue("Lớp:");
                row0.createCell(col++).setCellValue(className);
                col += 2;
                row0.createCell(col++).setCellValue("Môn:");
                row0.createCell(col++).setCellValue("Tin học");
                col += 2;
                row0.createCell(col++).setCellValue("HỌC KỲ I");
                col += 4;
                row0.createCell(col++).setCellValue("HỌC KỲ II");
                col += 3;
                row0.createCell(col++).setCellValue("GV : " + teacherName);
                col += 3;
                row0.createCell(col++).setCellValue("Lớp:");
                row0.createCell(col++).setCellValue(className);
                col += 1;
                row0.createCell(col++).setCellValue("Môn:");
                row0.createCell(col++).setCellValue("Tin học");
                col += 5;
                row0.createCell(col++).setCellValue("HỌC KỲ I");
                col += 12;
                row0.createCell(col++).setCellValue("HỌC KỲ II");
                col += 5;
                row0.createCell(col++).setCellValue("GV : " + teacherName);

                // Row 1: SS
                Row row1 = sheet.createRow(1);
                row1.createCell(0).setCellValue("SS:");
                row1.createCell(1).setCellValue(ss);
                row1.createCell(20).setCellValue("SS:");
                row1.createCell(21).setCellValue(ss);

                // Row 2: Column headers
                Row row2 = sheet.createRow(2);
                col = 0;
                row2.createCell(col++).setCellValue("TT");
                row2.createCell(col++).setCellValue("Họ và tên học sinh");
                row2.createCell(col++).setCellValue("ĐĐGtx");
                col += 4;
                row2.createCell(col++).setCellValue("ĐĐGgk");
                row2.createCell(col++).setCellValue("ĐĐGck");
                row2.createCell(col++).setCellValue("TBm HK1");
                row2.createCell(col++).setCellValue("Nhận xét");
                row2.createCell(col++).setCellValue("ĐĐGtx");
                col += 3;
                row2.createCell(col++).setCellValue("ĐĐGgk");
                row2.createCell(col++).setCellValue("ĐĐGck");
                row2.createCell(col++).setCellValue("TBm HK2");
                row2.createCell(col++).setCellValue("Nhận xét");
                row2.createCell(col++).setCellValue("TBm CN");

                // Second header part
                col = 20;
                row2.createCell(col++).setCellValue("TT");
                row2.createCell(col++).setCellValue("Họ và tên học sinh");
                row2.createCell(col++).setCellValue("ĐĐGtx");
                col += 5;
                row2.createCell(col++).setCellValue("ĐĐGgk");
                col += 1;
                row2.createCell(col++).setCellValue("ĐĐGck");
                col += 1;
                row2.createCell(col++).setCellValue("TBm HK1");
                col += 1;
                row2.createCell(col++).setCellValue("Nhận xét");
                col += 7;
                row2.createCell(col++).setCellValue("ĐĐGtx");
                col += 4;
                row2.createCell(col++).setCellValue("ĐĐGgk");
                row2.createCell(col++).setCellValue("ĐĐGck");
                row2.createCell(col++).setCellValue("TBm HK2");
                row2.createCell(col++).setCellValue("Nhận xét");
                row2.createCell(col++).setCellValue("TBm CN");

                // Student rows
                int rowIndex = 3;
                int tt = 1;
                for (Long studentId : sortedStudentIds) {
                    List<Score> studentScores = studentMap.get(studentId);
                    Score score1 = studentScores.stream().filter(s -> s.getSemester() == 1).findFirst().orElse(null);
                    Score score2 = studentScores.stream().filter(s -> s.getSemester() == 2).findFirst().orElse(null);

                    Row row = sheet.createRow(rowIndex++);
                    col = 0;
                    row.createCell(col++).setCellValue(tt);
                    String studentName = score1 != null ? score1.getStudentName() : (score2 != null ? score2.getStudentName() : "");
                    row.createCell(col++).setCellValue(studentName);

                    if (score1 != null) {
                        List<Integer> tx = score1.getDdgtx();
                        if (tx != null && !tx.isEmpty()) row.createCell(col++).setCellValue(tx.get(0));
                        if (tx != null && tx.size() > 1) row.createCell(col++).setCellValue(tx.get(1));
                        if (tx != null && tx.size() > 2) row.createCell(col++).setCellValue(tx.get(2));
                        col += 4 - Math.min(tx != null ? tx.size() : 0, 3); // Adjust for empty cells
                        row.createCell(col++).setCellValue(score1.getDdggk());
                        row.createCell(col++).setCellValue(score1.getDdgck());
                        row.createCell(col++).setCellValue(score1.getTbm());
                        row.createCell(col++).setCellValue(score1.getComment() != null ? score1.getComment() : "");
                    } else {
                        col += 9;
                    }

                    if (score2 != null) {
                        List<Integer> tx = score2.getDdgtx();
                        if (tx != null && !tx.isEmpty()) row.createCell(col++).setCellValue(tx.get(0));
                        if (tx != null && tx.size() > 1) row.createCell(col++).setCellValue(tx.get(1));
                        if (tx != null && tx.size() > 2) row.createCell(col++).setCellValue(tx.get(2));
                        col += 3 - Math.min(tx != null ? tx.size() : 0, 3); // Adjust for empty cells
                        row.createCell(col++).setCellValue(score2.getDdggk());
                        row.createCell(col++).setCellValue(score2.getDdgck());
                        row.createCell(col++).setCellValue(score2.getTbm());
                        row.createCell(col++).setCellValue(score2.getComment() != null ? score2.getComment() : "");
                    } else {
                        col += 8;
                    }

                    // TBm CN as average
                    double tbmCn = 0;
                    int count = 0;
                    if (score1 != null) {
                        tbmCn += score1.getTbm();
                        count++;
                    }
                    if (score2 != null) {
                        tbmCn += score2.getTbm();
                        count++;
                    }
                    if (count > 0) {
                        row.createCell(col++).setCellValue(Math.round(tbmCn / count * 10.0) / 10.0);
                    }

                    // Second part of the row (repeating for right side)
                    col = 20;
                    row.createCell(col++).setCellValue(tt);
                    row.createCell(col++).setCellValue(studentName);

                    if (score1 != null) {
                        List<Integer> tx = score1.getDdgtx();
                        if (tx != null && !tx.isEmpty()) row.createCell(col++).setCellValue(tx.get(0));
                        if (tx != null && tx.size() > 1) row.createCell(col++).setCellValue(tx.get(1));
                        if (tx != null && tx.size() > 2) row.createCell(col++).setCellValue(tx.get(2));
                        col += 5 - Math.min(tx != null ? tx.size() : 0, 3); // Adjust for empty cells
                        row.createCell(col++).setCellValue(score1.getDdggk());
                        col += 1;
                        row.createCell(col++).setCellValue(score1.getDdgck());
                        col += 1;
                        row.createCell(col++).setCellValue(score1.getTbm());
                        col += 1;
                        row.createCell(col++).setCellValue(score1.getComment() != null ? score1.getComment() : "");
                    } else {
                        col += 12;
                    }

                    if (score2 != null) {
                        List<Integer> tx = score2.getDdgtx();
                        if (tx != null && !tx.isEmpty()) row.createCell(col++).setCellValue(tx.get(0));
                        if (tx != null && tx.size() > 1) row.createCell(col++).setCellValue(tx.get(1));
                        if (tx != null && tx.size() > 2) row.createCell(col++).setCellValue(tx.get(2));
                        col += 4 - Math.min(tx != null ? tx.size() : 0, 3); // Adjust for empty cells
                        row.createCell(col++).setCellValue(score2.getDdggk());
                        row.createCell(col++).setCellValue(score2.getDdgck());
                        row.createCell(col++).setCellValue(score2.getTbm());
                        row.createCell(col++).setCellValue(score2.getComment() != null ? score2.getComment() : "");
                    }

                    // TBm CN as average
                    if (count > 0) {
                        row.createCell(col++).setCellValue(Math.round(tbmCn / count * 10.0) / 10.0);
                    }

                    tt++;
                }

                // Add empty rows to reach row 31
                while (rowIndex < 31) {
                    sheet.createRow(rowIndex++);
                }

                // Summary section
                Row row31 = sheet.createRow(30);
                row31.createCell(0).setCellValue(ss);
                int colOffset = 20;
                row31.createCell(colOffset++).setCellValue("TỔNG KẾT");
                row31.createCell(colOffset++).setCellValue("Thông tin điểm");
                colOffset += 5;
                row31.createCell(colOffset++).setCellValue("Học kỳ I");
                colOffset += 7;
                row31.createCell(colOffset++).setCellValue("Học kỳ II");

                Row row32 = sheet.createRow(31);
                col = colOffset - 15;
                row32.createCell(col++).setCellValue("Tổng");
                row32.createCell(col++).setCellValue("G");
                row32.createCell(col++).setCellValue("%");
                row32.createCell(col++).setCellValue("K");
                row32.createCell(col++).setCellValue("%");
                row32.createCell(col++).setCellValue("TB");
                row32.createCell(col++).setCellValue("%");
                row32.createCell(col++).setCellValue("Y");

                // Calculate statistics for HK1 and HK2
                List<Score> hk1Scores = classScores.stream().filter(s -> s.getSemester() == 1).collect(Collectors.toList());
                List<Score> hk2Scores = classScores.stream().filter(s -> s.getSemester() == 2).collect(Collectors.toList());

                // ĐĐGtx HK1
                int totalTx1 = 0, gTx1 = 0, kTx1 = 0, tbTx1 = 0, yTx1 = 0;
                for (Score s : hk1Scores) {
                    List<Integer> tx = s.getDdgtx();
                    if (tx != null) {
                        for (Integer sc : tx) {
                            totalTx1++;
                            if (sc >= 8) gTx1++;
                            else if (sc >= 7) kTx1++;
                            else if (sc >= 5) tbTx1++;
                            else yTx1++;
                        }
                    }
                }
                double pGTx1 = totalTx1 > 0 ? gTx1 * 100.0 / totalTx1 : 0;
                double pKTx1 = totalTx1 > 0 ? kTx1 * 100.0 / totalTx1 : 0;
                double pTbTx1 = totalTx1 > 0 ? tbTx1 * 100.0 / totalTx1 : 0;
                double pYTx1 = totalTx1 > 0 ? yTx1 * 100.0 / totalTx1 : 0;

                // ĐĐGtx HK2
                int totalTx2 = 0, gTx2 = 0, kTx2 = 0, tbTx2 = 0, yTx2 = 0;
                for (Score s : hk2Scores) {
                    List<Integer> tx = s.getDdgtx();
                    if (tx != null) {
                        for (Integer sc : tx) {
                            totalTx2++;
                            if (sc >= 8) gTx2++;
                            else if (sc >= 7) kTx2++;
                            else if (sc >= 5) tbTx2++;
                            else yTx2++;
                        }
                    }
                }
                double pGTx2 = totalTx2 > 0 ? gTx2 * 100.0 / totalTx2 : 0;
                double pKTx2 = totalTx2 > 0 ? kTx2 * 100.0 / totalTx2 : 0;
                double pTbTx2 = totalTx2 > 0 ? tbTx2 * 100.0 / totalTx2 : 0;
                double pYTx2 = totalTx2 > 0 ? yTx2 * 100.0 / totalTx2 : 0;

                // Row 33: ĐĐGtx
                Row row33 = sheet.createRow(32);
                col = colOffset - 15;
                row33.createCell(col++).setCellValue("Điểm ĐG thường xuyên");
                col += 6;
                row33.createCell(col++).setCellValue(totalTx1);
                row33.createCell(col++).setCellValue(gTx1);
                row33.createCell(col++).setCellValue(Math.round(pGTx1 * 10.0) / 10.0);
                row33.createCell(col++).setCellValue(kTx1);
                row33.createCell(col++).setCellValue(Math.round(pKTx1 * 10.0) / 10.0);
                row33.createCell(col++).setCellValue(tbTx1);
                row33.createCell(col++).setCellValue(Math.round(pTbTx1 * 10.0) / 10.0);
                row33.createCell(col++).setCellValue(yTx1);
                col += 2;
                row33.createCell(col++).setCellValue(totalTx2);
                row33.createCell(col++).setCellValue(gTx2);
                row33.createCell(col++).setCellValue(Math.round(pGTx2 * 10.0) / 10.0);
                row33.createCell(col++).setCellValue(kTx2);
                row33.createCell(col++).setCellValue(Math.round(pKTx2 * 10.0) / 10.0);
                row33.createCell(col++).setCellValue(tbTx2);
                row33.createCell(col++).setCellValue(Math.round(pTbTx2 * 10.0) / 10.0);
                row33.createCell(col++).setCellValue(yTx2);

                // ĐĐGgk HK1
                int totalGk1 = hk1Scores.size();
                int gGk1 = (int) hk1Scores.stream().filter(s -> s.getDdggk() >= 8).count();
                int kGk1 = (int) hk1Scores.stream().filter(s -> s.getDdggk() >= 7 && s.getDdggk() < 8).count();
                int tbGk1 = (int) hk1Scores.stream().filter(s -> s.getDdggk() >= 5 && s.getDdggk() < 7).count();
                int yGk1 = (int) hk1Scores.stream().filter(s -> s.getDdggk() < 5).count();
                double pGGk1 = totalGk1 > 0 ? gGk1 * 100.0 / totalGk1 : 0;
                double pKGk1 = totalGk1 > 0 ? kGk1 * 100.0 / totalGk1 : 0;
                double pTbGk1 = totalGk1 > 0 ? tbGk1 * 100.0 / totalGk1 : 0;
                double pYGk1 = totalGk1 > 0 ? yGk1 * 100.0 / totalGk1 : 0;

                // ĐĐGgk HK2
                int totalGk2 = hk2Scores.size();
                int gGk2 = (int) hk2Scores.stream().filter(s -> s.getDdggk() >= 8).count();
                int kGk2 = (int) hk2Scores.stream().filter(s -> s.getDdggk() >= 7 && s.getDdggk() < 8).count();
                int tbGk2 = (int) hk2Scores.stream().filter(s -> s.getDdggk() >= 5 && s.getDdggk() < 7).count();
                int yGk2 = (int) hk2Scores.stream().filter(s -> s.getDdggk() < 5).count();
                double pGGk2 = totalGk2 > 0 ? gGk2 * 100.0 / totalGk2 : 0;
                double pKGk2 = totalGk2 > 0 ? kGk2 * 100.0 / totalGk2 : 0;
                double pTbGk2 = totalGk2 > 0 ? tbGk2 * 100.0 / totalGk2 : 0;
                double pYGk2 = totalGk2 > 0 ? yGk2 * 100.0 / totalGk2 : 0;

                // Row 34: ĐĐGgk
                Row row34 = sheet.createRow(33);
                col = colOffset - 15;
                row34.createCell(col++).setCellValue("Điểm đánh giá giữa kỳ");
                col += 6;
                row34.createCell(col++).setCellValue(totalGk1);
                row34.createCell(col++).setCellValue(gGk1);
                row34.createCell(col++).setCellValue(Math.round(pGGk1 * 10.0) / 10.0);
                row34.createCell(col++).setCellValue(kGk1);
                row34.createCell(col++).setCellValue(Math.round(pKGk1 * 10.0) / 10.0);
                row34.createCell(col++).setCellValue(tbGk1);
                row34.createCell(col++).setCellValue(Math.round(pTbGk1 * 10.0) / 10.0);
                row34.createCell(col++).setCellValue(yGk1);
                col += 2;
                row34.createCell(col++).setCellValue(totalGk2);
                row34.createCell(col++).setCellValue(gGk2);
                row34.createCell(col++).setCellValue(Math.round(pGGk2 * 10.0) / 10.0);
                row34.createCell(col++).setCellValue(kGk2);
                row34.createCell(col++).setCellValue(Math.round(pKGk2 * 10.0) / 10.0);
                row34.createCell(col++).setCellValue(tbGk2);
                row34.createCell(col++).setCellValue(Math.round(pTbGk2 * 10.0) / 10.0);
                row34.createCell(col++).setCellValue(yGk2);

                // ĐĐGck HK1
                int totalCk1 = hk1Scores.size();
                int gCk1 = (int) hk1Scores.stream().filter(s -> s.getDdgck() >= 8).count();
                int kCk1 = (int) hk1Scores.stream().filter(s -> s.getDdgck() >= 7 && s.getDdgck() < 8).count();
                int tbCk1 = (int) hk1Scores.stream().filter(s -> s.getDdgck() >= 5 && s.getDdgck() < 7).count();
                int yCk1 = (int) hk1Scores.stream().filter(s -> s.getDdgck() < 5).count();
                double pGCk1 = totalCk1 > 0 ? gCk1 * 100.0 / totalCk1 : 0;
                double pKCk1 = totalCk1 > 0 ? kCk1 * 100.0 / totalCk1 : 0;
                double pTbCk1 = totalCk1 > 0 ? tbCk1 * 100.0 / totalCk1 : 0;
                double pYCk1 = totalCk1 > 0 ? yCk1 * 100.0 / totalCk1 : 0;

                // ĐĐGck HK2
                int totalCk2 = hk2Scores.size();
                int gCk2 = (int) hk2Scores.stream().filter(s -> s.getDdgck() >= 8).count();
                int kCk2 = (int) hk2Scores.stream().filter(s -> s.getDdgck() >= 7 && s.getDdgck() < 8).count();
                int tbCk2 = (int) hk2Scores.stream().filter(s -> s.getDdgck() >= 5 && s.getDdgck() < 7).count();
                int yCk2 = (int) hk2Scores.stream().filter(s -> s.getDdgck() < 5).count();
                double pGCk2 = totalCk2 > 0 ? gCk2 * 100.0 / totalCk2 : 0;
                double pKCk2 = totalCk2 > 0 ? kCk2 * 100.0 / totalCk2 : 0;
                double pTbCk2 = totalCk2 > 0 ? tbCk2 * 100.0 / totalCk2 : 0;
                double pYCk2 = totalCk2 > 0 ? yCk2 * 100.0 / totalCk2 : 0;

                // Row 35: ĐĐGck
                Row row35 = sheet.createRow(34);
                col = colOffset - 15;
                row35.createCell(col++).setCellValue("Điểm đánh giá cuối kỳ");
                col += 6;
                row35.createCell(col++).setCellValue(totalCk1);
                row35.createCell(col++).setCellValue(gCk1);
                row35.createCell(col++).setCellValue(Math.round(pGCk1 * 10.0) / 10.0);
                row35.createCell(col++).setCellValue(kCk1);
                row35.createCell(col++).setCellValue(Math.round(pKCk1 * 10.0) / 10.0);
                row35.createCell(col++).setCellValue(tbCk1);
                row35.createCell(col++).setCellValue(Math.round(pTbCk1 * 10.0) / 10.0);
                row35.createCell(col++).setCellValue(yCk1);
                col += 2;
                row35.createCell(col++).setCellValue(totalCk2);
                row35.createCell(col++).setCellValue(gCk2);
                row35.createCell(col++).setCellValue(Math.round(pGCk2 * 10.0) / 10.0);
                row35.createCell(col++).setCellValue(kCk2);
                row35.createCell(col++).setCellValue(Math.round(pKCk2 * 10.0) / 10.0);
                row35.createCell(col++).setCellValue(tbCk2);
                row35.createCell(col++).setCellValue(Math.round(pTbCk2 * 10.0) / 10.0);
                row35.createCell(col++).setCellValue(yCk2);

                // TBM HK1
                int totalTbm1 = hk1Scores.size();
                int gTbm1 = (int) hk1Scores.stream().filter(s -> s.getTbm() >= 8).count();
                int kTbm1 = (int) hk1Scores.stream().filter(s -> s.getTbm() >= 7 && s.getTbm() < 8).count();
                int tbTbm1 = (int) hk1Scores.stream().filter(s -> s.getTbm() >= 5 && s.getTbm() < 7).count();
                int yTbm1 = (int) hk1Scores.stream().filter(s -> s.getTbm() < 5).count();
                double pGTbm1 = totalTbm1 > 0 ? gTbm1 * 100.0 / totalTbm1 : 0;
                double pKTbm1 = totalTbm1 > 0 ? kTbm1 * 100.0 / totalTbm1 : 0;
                double pTbTbm1 = totalTbm1 > 0 ? tbTbm1 * 100.0 / totalTbm1 : 0;
                double pYTbm1 = totalTbm1 > 0 ? yTbm1 * 100.0 / totalTbm1 : 0;

                // TBM HK2
                int totalTbm2 = hk2Scores.size();
                int gTbm2 = (int) hk2Scores.stream().filter(s -> s.getTbm() >= 8).count();
                int kTbm2 = (int) hk2Scores.stream().filter(s -> s.getTbm() >= 7 && s.getTbm() < 8).count();
                int tbTbm2 = (int) hk2Scores.stream().filter(s -> s.getTbm() >= 5 && s.getTbm() < 7).count();
                int yTbm2 = (int) hk2Scores.stream().filter(s -> s.getTbm() < 5).count();
                double pGTbm2 = totalTbm2 > 0 ? gTbm2 * 100.0 / totalTbm2 : 0;
                double pKTbm2 = totalTbm2 > 0 ? kTbm2 * 100.0 / totalTbm2 : 0;
                double pTbTbm2 = totalTbm2 > 0 ? tbTbm2 * 100.0 / totalTbm2 : 0;
                double pYTbm2 = totalTbm2 > 0 ? yTbm2 * 100.0 / totalTbm2 : 0;

                // Row 36: TBM
                Row row36 = sheet.createRow(35);
                col = colOffset - 15;
                row36.createCell(col++).setCellValue("TBM");
                col += 6;
                row36.createCell(col++).setCellValue(totalTbm1);
                row36.createCell(col++).setCellValue(gTbm1);
                row36.createCell(col++).setCellValue(Math.round(pGTbm1 * 10.0) / 10.0);
                row36.createCell(col++).setCellValue(kTbm1);
                row36.createCell(col++).setCellValue(Math.round(pKTbm1 * 10.0) / 10.0);
                row36.createCell(col++).setCellValue(tbTbm1);
                row36.createCell(col++).setCellValue(Math.round(pTbTbm1 * 10.0) / 10.0);
                row36.createCell(col++).setCellValue(yTbm1);
                col += 2;
                row36.createCell(col++).setCellValue(totalTbm2);
                row36.createCell(col++).setCellValue(gTbm2);
                row36.createCell(col++).setCellValue(Math.round(pGTbm2 * 10.0) / 10.0);
                row36.createCell(col++).setCellValue(kTbm2);
                row36.createCell(col++).setCellValue(Math.round(pKTbm2 * 10.0) / 10.0);
                row36.createCell(col++).setCellValue(tbTbm2);
                row36.createCell(col++).setCellValue(Math.round(pTbTbm2 * 10.0) / 10.0);
                row36.createCell(col++).setCellValue(yTbm2);

                // Set column widths
                for (int i = 0; i < 60; i++) {
                    sheet.setColumnWidth(i, 2000);
                }
                sheet.setColumnWidth(1, 8000);
                sheet.setColumnWidth(21, 8000);
            }

            // Write workbook to response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=so_diem_ca_nhan_.xlsx");
            workbook.write(response.getOutputStream());
        } finally {
            // Ensure workbook is closed to free resources
            try {
                workbook.close();
            } catch (IOException e) {
                throw new IOException("Failed to close workbook", e);
            }
        }
    }
    
    @Override
    public List<Score> importFromExcel(MultipartFile file) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'importFromExcel'");
    }
}