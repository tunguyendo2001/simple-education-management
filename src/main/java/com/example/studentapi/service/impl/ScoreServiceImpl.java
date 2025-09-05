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

                // Create a sheet for each class
                Sheet sheet = workbook.createSheet(className);

                // Get teacher name (assume consistent for class)
                String teacherName = classScores.get(0).getTeacherName() != null ? classScores.get(0).getTeacherName()
                        : "Nguyễn Thị Thủy";

                // Group by studentId to get unique students
                Map<Long, List<Score>> studentMap = classScores.stream()
                        .collect(Collectors.groupingBy(Score::getStudentId));

                // Sort students by name
                List<Long> sortedStudentIds = new ArrayList<>(studentMap.keySet());
                sortedStudentIds.sort((id1, id2) -> {
                    String name1 = studentMap.get(id1).get(0).getStudentName();
                    String name2 = studentMap.get(id2).get(0).getStudentName();
                    return name1.compareTo(name2);
                });

                // Create header section
                createHeader(sheet, className, teacherName, sortedStudentIds.size());

                // Create column headers
                createColumnHeaders(sheet);

                // Fill student data
                fillStudentData(sheet, studentMap, sortedStudentIds);

                // Auto-size columns for better readability
                autoSizeColumns(sheet);
            }

            // Write workbook to response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=so_diem_ca_nhan.xlsx");
            workbook.write(response.getOutputStream());

        } finally {
            workbook.close();
        }
    }

    private void createHeader(Sheet sheet, String className, String teacherName, int studentCount) {
        // Row 0: Main header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Lớp:");
        headerRow.createCell(1).setCellValue(className);
        headerRow.createCell(4).setCellValue("Môn:");
        headerRow.createCell(5).setCellValue("Tin học");
        headerRow.createCell(8).setCellValue("HỌC KỲ I");
        headerRow.createCell(12).setCellValue("HỌC KỲ II");
        headerRow.createCell(16).setCellValue("GV: " + teacherName);

        // Row 1: Student count
        Row countRow = sheet.createRow(1);
        countRow.createCell(0).setCellValue("SS:");
        countRow.createCell(1).setCellValue(studentCount);
    }

    private void createColumnHeaders(Sheet sheet) {
        // Row 2: Column headers
        Row headerRow = sheet.createRow(2);

        String[] headers = {
                "TT", "Họ và tên học sinh",
                "ĐĐGtx", "ĐĐGek", "ĐĐGck", "TBm HK1", "Nhận xét",
                "ĐĐGtx", "ĐĐGek", "ĐĐGck", "TBm HK2", "Nhận xét", "TBm CN"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            // Style header cells
            CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
            Font headerFont = sheet.getWorkbook().createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillStudentData(Sheet sheet, Map<Long, List<Score>> studentMap, List<Long> sortedStudentIds) {
        int rowIndex = 3; // Start from row 3 (after headers)
        int studentNumber = 1;

        CellStyle dataStyle = sheet.getWorkbook().createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle nameStyle = sheet.getWorkbook().createCellStyle();
        nameStyle.setBorderBottom(BorderStyle.THIN);
        nameStyle.setBorderTop(BorderStyle.THIN);
        nameStyle.setBorderLeft(BorderStyle.THIN);
        nameStyle.setBorderRight(BorderStyle.THIN);
        nameStyle.setAlignment(HorizontalAlignment.LEFT);

        for (Long studentId : sortedStudentIds) {
            List<Score> studentScores = studentMap.get(studentId);

            // Get scores for semester 1 and 2
            Score hk1Score = studentScores.stream()
                    .filter(s -> s.getSemester() == 1)
                    .findFirst().orElse(null);
            Score hk2Score = studentScores.stream()
                    .filter(s -> s.getSemester() == 2)
                    .findFirst().orElse(null);

            Row row = sheet.createRow(rowIndex++);
            int colIndex = 0;

            // TT (student number)
            Cell ttCell = row.createCell(colIndex++);
            ttCell.setCellValue(studentNumber++);
            ttCell.setCellStyle(dataStyle);

            // Student name
            String studentName = hk1Score != null ? hk1Score.getStudentName()
                    : (hk2Score != null ? hk2Score.getStudentName() : "");
            Cell nameCell = row.createCell(colIndex++);
            nameCell.setCellValue(studentName);
            nameCell.setCellStyle(nameStyle);

            // HK1 scores
            if (hk1Score != null) {
                // ĐĐGtx - average of regular scores
                double avgTx1 = hk1Score.getDdgtx() != null && !hk1Score.getDdgtx().isEmpty()
                        ? hk1Score.getDdgtx().stream().mapToInt(Integer::intValue).average().orElse(0.0)
                        : 0.0;
                Cell txCell1 = row.createCell(colIndex++);
                txCell1.setCellValue(Math.round(avgTx1 * 10.0) / 10.0);
                txCell1.setCellStyle(dataStyle);

                // ĐĐGek (mid-term)
                Cell gkCell1 = row.createCell(colIndex++);
                gkCell1.setCellValue(hk1Score.getDdggk());
                gkCell1.setCellStyle(dataStyle);

                // ĐĐGck (final)
                Cell ckCell1 = row.createCell(colIndex++);
                ckCell1.setCellValue(hk1Score.getDdgck());
                ckCell1.setCellStyle(dataStyle);

                // TBm HK1
                Cell tbmCell1 = row.createCell(colIndex++);
                tbmCell1.setCellValue(hk1Score.getTbm());
                tbmCell1.setCellStyle(dataStyle);

                // Comment HK1
                Cell commentCell1 = row.createCell(colIndex++);
                commentCell1.setCellValue(hk1Score.getComment() != null ? hk1Score.getComment() : "");
                commentCell1.setCellStyle(dataStyle);
            } else {
                // Empty cells for HK1 if no data
                for (int i = 0; i < 5; i++) {
                    Cell emptyCell = row.createCell(colIndex++);
                    emptyCell.setCellStyle(dataStyle);
                }
            }

            // HK2 scores
            if (hk2Score != null) {
                // ĐĐGtx - average of regular scores
                double avgTx2 = hk2Score.getDdgtx() != null && !hk2Score.getDdgtx().isEmpty()
                        ? hk2Score.getDdgtx().stream().mapToInt(Integer::intValue).average().orElse(0.0)
                        : 0.0;
                Cell txCell2 = row.createCell(colIndex++);
                txCell2.setCellValue(Math.round(avgTx2 * 10.0) / 10.0);
                txCell2.setCellStyle(dataStyle);

                // ĐĐGek (mid-term)
                Cell gkCell2 = row.createCell(colIndex++);
                gkCell2.setCellValue(hk2Score.getDdggk());
                gkCell2.setCellStyle(dataStyle);

                // ĐĐGck (final)
                Cell ckCell2 = row.createCell(colIndex++);
                ckCell2.setCellValue(hk2Score.getDdgck());
                ckCell2.setCellStyle(dataStyle);

                // TBm HK2
                Cell tbmCell2 = row.createCell(colIndex++);
                tbmCell2.setCellValue(hk2Score.getTbm());
                tbmCell2.setCellStyle(dataStyle);

                // Comment HK2
                Cell commentCell2 = row.createCell(colIndex++);
                commentCell2.setCellValue(hk2Score.getComment() != null ? hk2Score.getComment() : "");
                commentCell2.setCellStyle(dataStyle);
            } else {
                // Empty cells for HK2 if no data
                for (int i = 0; i < 5; i++) {
                    Cell emptyCell = row.createCell(colIndex++);
                    emptyCell.setCellStyle(dataStyle);
                }
            }

            // TBm CN (yearly average)
            double yearlyAvg = 0.0;
            int count = 0;
            if (hk1Score != null) {
                yearlyAvg += hk1Score.getTbm();
                count++;
            }
            if (hk2Score != null) {
                yearlyAvg += hk2Score.getTbm();
                count++;
            }
            if (count > 0) {
                Cell avgCell = row.createCell(colIndex++);
                avgCell.setCellValue(Math.round((yearlyAvg / count) * 10.0) / 10.0);
                avgCell.setCellStyle(dataStyle);
            } else {
                Cell avgCell = row.createCell(colIndex++);
                avgCell.setCellStyle(dataStyle);
            }
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        // Auto-size columns for better readability
        for (int i = 0; i < 13; i++) {
            sheet.autoSizeColumn(i);
            // Set minimum width
            if (sheet.getColumnWidth(i) < 2000) {
                sheet.setColumnWidth(i, 2000);
            }
            // Set maximum width for name column
            if (i == 1 && sheet.getColumnWidth(i) > 8000) {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }

    @Override
    public List<Score> importFromExcel(MultipartFile file) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'importFromExcel'");
    }
}