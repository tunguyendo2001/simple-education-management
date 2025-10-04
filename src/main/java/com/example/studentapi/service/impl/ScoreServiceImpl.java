package com.example.studentapi.service.impl;

import com.example.studentapi.model.SchoolClass;
import com.example.studentapi.model.Score;
import com.example.studentapi.repository.ClassRepository;
import com.example.studentapi.repository.ScoreRepository;
import com.example.studentapi.repository.TeacherClassAssignmentRepository;
import com.example.studentapi.service.ScoreService;
import com.example.studentapi.service.SchoolClassService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private TeacherClassAssignmentRepository teacherClassAssignmentRepository;
    
    @Autowired
    private SchoolClassService classService;

    @Autowired
    private ClassRepository classRepository;

    // ========== BASIC CRUD OPERATIONS ==========
    
    @Override
    public Score findById(String id) {
        return scoreRepository.findById(id).orElse(null);
    }

    @Override
    public List<Score> findAll() {
        return scoreRepository.findAll();
    }

    @Override
    public Score save(Score score) {
        validateScoreData(score);
        
        if (score.getClassId() == null && score.getClassName() != null) {
            SchoolClass schoolClass = classRepository.findByClassName(score.getClassName()).orElse(null);
            if (schoolClass != null) {
                score.setClassId(schoolClass.getId());
            }
        }

        // Generate custom ID if not provided
        if (score.getId() == null || score.getId().trim().isEmpty()) {
            String scoreId = Score.createScoreId(
                score.getTeacherId(), 
                score.getStudentId(), 
                score.getClassName(), 
                score.getSubject(), 
                score.getYear(), 
                score.getSemester()
            );
            score.setId(scoreId);
        }

        // Auto-calculate TBM if not provided
        if (score.getTbm() == null || score.getTbm() == 0.0) {
            score.calculateTbm();
        }
        
        return scoreRepository.save(score);
    }

    @Override
    public Score update(String id, Score score) {
        if (!scoreRepository.existsById(id)) {
            return null;
        }
        
        validateScoreData(score);

        if (score.getClassId() == null && score.getClassName() != null) {
            SchoolClass schoolClass = classRepository.findByClassName(score.getClassName()).orElse(null);
            if (schoolClass != null) {
                score.setClassId(schoolClass.getId());
            }
        }
        
        // Auto-calculate TBM
        score.calculateTbm();
        
        score.setId(id);
        return scoreRepository.save(score);
    }

    @Override
    public void delete(String id) {
        scoreRepository.deleteById(id);
    }

    // ========== BATCH OPERATIONS ==========
    
    @Override
    public List<Score> saveAll(List<Score> scores) {
        List<Score> validatedScores = new ArrayList<>();
        
        for (Score score : scores) {
            try {
                validateScoreData(score);

                if (score.getClassId() == null && score.getClassName() != null) {
                    SchoolClass schoolClass = classRepository.findByClassName(score.getClassName()).orElse(null);
                    if (schoolClass != null) {
                        score.setClassId(schoolClass.getId());
                    }
                }

                // Generate custom ID if not provided
                if (score.getId() == null || score.getId().trim().isEmpty()) {
                    String customId = Score.createScoreId(
                        score.getTeacherId(), 
                        score.getStudentId(), 
                        score.getClassName(), 
                        score.getSubject(), 
                        score.getYear(), 
                        score.getSemester()
                    );
                    score.setId(customId);
                }
                
                // Auto-calculate TBM if not provided
                if (score.getTbm() == null || score.getTbm() == 0.0) {
                    score.calculateTbm();
                }
                
                validatedScores.add(score);
            } catch (IllegalArgumentException e) {
                // Skip invalid scores or throw exception based on requirements
                throw new IllegalArgumentException("Invalid score data: " + e.getMessage());
            }
        }
        
        return scoreRepository.saveAll(validatedScores);
    }

    @Override
    public List<Score> updateAll(List<Score> scores) {
        List<Score> updatedScores = new ArrayList<>();
        
        for (Score score : scores) {
            if (score.getId() != null && scoreRepository.existsById(score.getId())) {
                validateScoreData(score);

                if (score.getClassId() == null && score.getClassName() != null) {
                    SchoolClass schoolClass = classRepository.findByClassName(score.getClassName()).orElse(null);
                    if (schoolClass != null) {
                        score.setClassId(schoolClass.getId());
                    }
                }
                
                score.calculateTbm();
                updatedScores.add(score);
            }
        }
        
        return scoreRepository.saveAll(updatedScores);
    }

    // ========== IMPORT/EXPORT OPERATIONS ==========
    
    @Override
    public List<Score> importFromExcel(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        List<Score> scores = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Score score = parseRowToScore(row);
                    if (score != null) {
                        scores.add(score);
                    }
                } catch (Exception e) {
                    // Log error but continue processing other rows
                    System.err.println("Error processing row " + i + ": " + e.getMessage());
                }
            }
        }
        
        return saveAll(scores);
    }
    
    private Score parseRowToScore(Row row) {
        try {
            Score score = new Score();
            
            // Assuming Excel columns: StudentId, TeacherId, ClassName, Subject, Semester, Year, etc.
            score.setStudentId((long) row.getCell(0).getNumericCellValue());
            score.setTeacherId((long) row.getCell(1).getNumericCellValue());
            score.setClassName(row.getCell(2).getStringCellValue());
            score.setSubject(row.getCell(3).getStringCellValue());
            score.setSemester(row.getCell(4).getStringCellValue());
            score.setYear((int) row.getCell(5).getNumericCellValue());
            
            // Parse regular scores (ddgtx) - assuming comma-separated in one cell
            String ddgtxString = row.getCell(6).getStringCellValue();
            if (ddgtxString != null && !ddgtxString.trim().isEmpty()) {
                List<Integer> ddgtx = parseCommaSeparatedScores(ddgtxString);
                score.setDdgtxList(ddgtx);
            }
            
            // Parse other scores
            Cell ddggkCell = row.getCell(7);
            if (ddggkCell != null) {
                score.setDdggk((int) ddggkCell.getNumericCellValue());
            }
            
            Cell ddgckCell = row.getCell(8);
            if (ddgckCell != null) {
                score.setDdgck((int) ddgckCell.getNumericCellValue());
            }
            
            // Student and teacher names
            Cell studentNameCell = row.getCell(9);
            if (studentNameCell != null) {
                score.setStudentName(studentNameCell.getStringCellValue());
            }
            
            Cell teacherNameCell = row.getCell(10);
            if (teacherNameCell != null) {
                score.setTeacherName(teacherNameCell.getStringCellValue());
            }
            
            // Comment
            Cell commentCell = row.getCell(11);
            if (commentCell != null) {
                score.setComment(commentCell.getStringCellValue());
            }
            
            // Auto-calculate TBM
            score.calculateTbm();
            
            return score;
            
        } catch (Exception e) {
            throw new RuntimeException("Error parsing row: " + e.getMessage(), e);
        }
    }
    
    private List<Integer> parseCommaSeparatedScores(String scoresString) {
        List<Integer> scores = new ArrayList<>();
        if (scoresString != null && !scoresString.trim().isEmpty()) {
            String[] scoreArray = scoresString.split(",");
            for (String scoreStr : scoreArray) {
                try {
                    int score = Integer.parseInt(scoreStr.trim());
                    if (score >= 0 && score <= 10) {
                        scores.add(score);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid scores
                }
            }
        }
        return scores;
    }

    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Score> scores = scoreRepository.findAll();
        exportScoresToExcel(scores, response, "all_scores.xlsx");
    }

    @Override
    public void exportToExcelForTeacher(HttpServletResponse response, Long teacherId) throws IOException {
        List<Score> scores = scoreRepository.findByTeacherId(teacherId);
        exportScoresToExcel(scores, response, "scores_teacher_" + teacherId + ".xlsx");
    }
    
    private void exportScoresToExcel(List<Score> scores, HttpServletResponse response, String filename) throws IOException {
        if (scores.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No score data available to export");
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        try {
            // Group scores by className and subject
            Map<String, Map<String, List<Score>>> classSubjectMap = scores.stream()
                    .collect(Collectors.groupingBy(
                        Score::getClassName,
                        Collectors.groupingBy(Score::getSubject)
                    ));

            for (Map.Entry<String, Map<String, List<Score>>> classEntry : classSubjectMap.entrySet()) {
                String className = classEntry.getKey();
                
                for (Map.Entry<String, List<Score>> subjectEntry : classEntry.getValue().entrySet()) {
                    String subject = subjectEntry.getKey();
                    List<Score> classSubjectScores = subjectEntry.getValue();
                    
                    if (classSubjectScores.isEmpty()) continue;

                    // Create sheet name with class and subject
                    String sheetName = className + "_" + subject;
                    if (sheetName.length() > 31) { // Excel sheet name limit
                        sheetName = sheetName.substring(0, 31);
                    }
                    
                    Sheet sheet = workbook.createSheet(sheetName);
                    String teacherName = classSubjectScores.get(0).getTeacherName() != null ? 
                        classSubjectScores.get(0).getTeacherName() : "Teacher";

                    // Group by studentId
                    Map<Long, List<Score>> studentMap = classSubjectScores.stream()
                            .collect(Collectors.groupingBy(Score::getStudentId));

                    List<Long> sortedStudentIds = new ArrayList<>(studentMap.keySet());
                    sortedStudentIds.sort((id1, id2) -> {
                        String name1 = studentMap.get(id1).get(0).getStudentName();
                        String name2 = studentMap.get(id2).get(0).getStudentName();
                        return name1 != null && name2 != null ? name1.compareTo(name2) : 0;
                    });

                    createHeader(sheet, className, teacherName, sortedStudentIds.size(), subject);
                    createColumnHeaders(sheet);
                    fillStudentData(sheet, studentMap, sortedStudentIds);
                    autoSizeColumns(sheet);
                }
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            workbook.write(response.getOutputStream());

        } finally {
            workbook.close();
        }
    }

    // ========== QUERY METHODS - BASIC ==========
    
    @Override
    public List<Score> findByStudentId(Long studentId) {
        return scoreRepository.findByStudentId(studentId);
    }

    @Override
    public List<Score> findByTeacherId(Long teacherId) {
        return scoreRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<Score> findByClassName(String className) {
        return scoreRepository.findByClassName(className);
    }

    @Override
    public List<Score> findByYearAndSemester(int year, String semester) {
        return scoreRepository.findByYearAndSemester(year, semester);
    }

    @Override
    public List<Score> findByClassNameAndYearAndSemester(String className, int year, String semester) {
        return scoreRepository.findByClassNameAndYearAndSemester(className, year, semester);
    }

    // ========== QUERY METHODS - WITH SUBJECT SUPPORT ==========
    
    @Override
    public List<Score> findBySubject(String subject) {
        return scoreRepository.findBySubject(subject);
    }

    @Override
    public List<Score> findByClassNameAndSubject(String className, String subject) {
        return scoreRepository.findByClassNameAndSubject(className, subject);
    }

    @Override
    public List<Score> findByTeacherIdAndSubject(Long teacherId, String subject) {
        return scoreRepository.findByTeacherIdAndSubject(teacherId, subject);
    }

    @Override
    public List<Score> findByStudentIdAndSubject(Long studentId, String subject) {
        return scoreRepository.findByStudentIdAndSubject(studentId, subject);
    }

    @Override
    public List<Score> findByClassNameAndSubjectAndYearAndSemester(String className, String subject, int year, String semester) {
        return scoreRepository.findByClassNameAndSubjectAndYearAndSemester(className, subject, year, semester);
    }

    // ========== ADVANCED QUERY METHODS ==========
    
    @Override
    public List<Score> findByTeacherIdAndClassNameAndSubject(Long teacherId, String className, String subject) {
        return scoreRepository.findByTeacherIdAndClassNameAndSubject(teacherId, className, subject);
    }

    @Override
    public List<Score> findByStudentIdAndYearAndSemester(Long studentId, int year, String semester) {
        return scoreRepository.findByStudentIdAndYearAndSemester(studentId, year, semester);
    }

    @Override
    public List<Score> findByTeacherIdAndYearAndSemester(Long teacherId, int year, String semester) {
        return scoreRepository.findByTeacherIdAndYearAndSemester(teacherId, year, semester);
    }

    // ========== SECURITY METHODS ==========
    
    @Override
    public boolean teacherHasAccessToScore(Long teacherId, String scoreId) {
        return scoreRepository.existsByIdAndTeacherId(scoreId, teacherId);
    }

    @Override
    public boolean teacherHasAccessToClass(Long teacherId, String className, String subject, Integer academicYear, String semester) {
        return teacherClassAssignmentRepository.teacherHasAccessToClass(teacherId, className, subject, academicYear, semester);
    }

    @Override
    public boolean teacherCanCreateScoreForStudent(Long teacherId, Long studentId, String className, String subject, Integer academicYear, String semester) {
        // Check if teacher has access to the class
        if (!teacherHasAccessToClass(teacherId, className, subject, academicYear, semester)) {
            return false;
        }
        
        // Additional logic can be added here to check if student is in the class
        // This would require integration with StudentClassAssignment
        return classService.teacherHasAccessToClass(teacherId, null); // You'd need to get classId from className
    }

    // ========== STATISTICAL METHODS ==========
    
    @Override
    public double getAverageScoreForClass(String className, String subject, int year, String semester) {
        Double average = scoreRepository.getAverageScoreForClass(className, subject, year, semester);
        return average != null ? average : 0.0;
    }

    @Override
    public long getStudentCountForClass(String className, String subject, int year, String semester) {
        Long count = scoreRepository.getStudentCountForClass(className, subject, year, semester);
        return count != null ? count : 0;
    }

    @Override
    public List<Score> getTopScoresForClass(String className, String subject, int year, String semester, int limit) {
        List<Score> allScores = scoreRepository.findTopScoresForClass(className, subject, year, semester);
        return allScores.stream().limit(limit).collect(Collectors.toList());
    }

    // ========== VALIDATION METHODS ==========
    
    @Override
    public boolean validateScoreData(Score score) {
        List<String> errors = validateScoreInternal(score);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
        return true;
    }

    @Override
    public List<String> validateScoreList(List<Score> scores) {
        List<String> allErrors = new ArrayList<>();
        
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            List<String> scoreErrors = validateScoreInternal(score);
            
            for (String error : scoreErrors) {
                allErrors.add("Score " + (i + 1) + ": " + error);
            }
        }
        
        return allErrors;
    }
    
    private List<String> validateScoreInternal(Score score) {
        List<String> errors = new ArrayList<>();
        
        // Required fields validation
        if (score.getStudentId() == null) {
            errors.add("Student ID is required");
        }
        if (score.getTeacherId() == null) {
            errors.add("Teacher ID is required");
        }
        if (score.getClassName() == null || score.getClassName().trim().isEmpty()) {
            errors.add("Class name is required");
        }
        if (score.getSubject() == null || score.getSubject().trim().isEmpty()) {
            errors.add("Subject is required");
        }
        if (score.getSemester() == null || score.getSemester().trim().isEmpty()) {
            errors.add("Semester is required");
        }
        if (score.getYear() <= 0) {
            errors.add("Valid year is required");
        }
        
        // Score range validation
        if (score.getDdggk() != null && (score.getDdggk() < 0 || score.getDdggk() > 10)) {
            errors.add("Mid-term score must be between 0 and 10");
        }
        if (score.getDdgck() != null && (score.getDdgck() < 0 || score.getDdgck() > 10)) {
            errors.add("Final score must be between 0 and 10");
        }
        if (score.getTbm() != null && (score.getTbm() < 0 || score.getTbm() > 10)) {
            errors.add("Average score must be between 0 and 10");
        }
        
        // Regular scores validation
        List<Integer> ddgtx = score.getDdgtxList();
        if (ddgtx != null) {
            for (int i = 0; i < ddgtx.size(); i++) {
                Integer regularScore = ddgtx.get(i);
                if (regularScore < 0 || regularScore > 10) {
                    errors.add("Regular score " + (i + 1) + " must be between 0 and 10");
                }
            }
        }
        
        // Semester validation
        if (score.getSemester() != null && !score.getSemester().matches("[123]")) {
            errors.add("Semester must be '1' or '2' or '3'");
        }
        
        // // Check for duplicate scores (same student, class, subject, year, semester)
        // if (score.getId() == null) { // Only check for new scores
        //     List<Score> duplicates = scoreRepository.findDuplicateScore(
        //         score.getStudentId(), score.getClassName(), score.getSubject(), 
        //         score.getYear(), score.getSemester());
        //     if (!duplicates.isEmpty()) {
        //         errors.add("Score already exists for this student in the same class, subject, year, and semester");
        //     }
        // } else { // Check for existing scores excluding current one
        //     boolean hasDuplicate = scoreRepository.existsDuplicateScore(
        //         score.getStudentId(), score.getClassName(), score.getSubject(), 
        //         score.getYear(), score.getSemester(), score.getId());
        //     if (hasDuplicate) {
        //         errors.add("Another score already exists for this student in the same class, subject, year, and semester");
        //     }
        // }
        
        return errors;
    }

    // ========== HELPER METHODS FOR EXCEL EXPORT ==========
    
    private void createHeader(Sheet sheet, String className, String teacherName, int studentCount, String subject) {
        // Row 0: Main header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Lớp:");
        headerRow.createCell(1).setCellValue(className);
        headerRow.createCell(4).setCellValue("Môn:");
        headerRow.createCell(5).setCellValue(subject != null ? subject : "Tin học");
        headerRow.createCell(13).setCellValue("GV: " + teacherName);

        // Row 1: Student count
        Row countRow = sheet.createRow(1);
        countRow.createCell(0).setCellValue("SS:");
        countRow.createCell(1).setCellValue(studentCount);
        countRow.createCell(2).setCellValue("HỌC KỲ I");
        countRow.createCell(7).setCellValue("HỌC KỲ II");
    }
    
    // Overloaded method for backward compatibility
    private void createHeader(Sheet sheet, String className, String teacherName, int studentCount) {
        createHeader(sheet, className, teacherName, studentCount, "Tin học");
    }

    private void createColumnHeaders(Sheet sheet) {
        // Row 2: Column headers
        Row headerRow = sheet.createRow(2);

        String[] headers = {
                "TT", "Họ và tên học sinh",
                "ĐĐGtx", "ĐĐGgk", "ĐĐGck", "TBm HK1", "Nhận xét",
                "ĐĐGtx", "ĐĐGgk", "ĐĐGck", "TBm HK2", "Nhận xét", "TBm CN"
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
                    .filter(s -> "1".equals(s.getSemester()))
                    .findFirst().orElse(null);
            Score hk2Score = studentScores.stream()
                    .filter(s -> "2".equals(s.getSemester()))
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
            nameCell.setCellValue(studentName != null ? studentName : "");
            nameCell.setCellStyle(nameStyle);

            // HK1 scores
            if (hk1Score != null) {
                // ĐĐGtx - average of regular scores
                double avgTx1 = hk1Score.getDdgtxList() != null && !hk1Score.getDdgtxList().isEmpty()
                        ? hk1Score.getDdgtxList().stream().mapToInt(Integer::intValue).average().orElse(0.0)
                        : 0.0;
                Cell txCell1 = row.createCell(colIndex++);
                txCell1.setCellValue(Math.round(avgTx1 * 10.0) / 10.0);
                txCell1.setCellStyle(dataStyle);

                // ĐĐGgk (mid-term)
                Cell gkCell1 = row.createCell(colIndex++);
                gkCell1.setCellValue(hk1Score.getDdggk() != null ? hk1Score.getDdggk() : 0);
                gkCell1.setCellStyle(dataStyle);

                // ĐĐGck (final)
                Cell ckCell1 = row.createCell(colIndex++);
                ckCell1.setCellValue(hk1Score.getDdgck() != null ? hk1Score.getDdgck() : 0);
                ckCell1.setCellStyle(dataStyle);

                // TBm HK1
                Cell tbmCell1 = row.createCell(colIndex++);
                tbmCell1.setCellValue(hk1Score.getTbm() != null ? hk1Score.getTbm() : 0.0);
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
                double avgTx2 = hk2Score.getDdgtxList() != null && !hk2Score.getDdgtxList().isEmpty()
                        ? hk2Score.getDdgtxList().stream().mapToInt(Integer::intValue).average().orElse(0.0)
                        : 0.0;
                Cell txCell2 = row.createCell(colIndex++);
                txCell2.setCellValue(Math.round(avgTx2 * 10.0) / 10.0);
                txCell2.setCellStyle(dataStyle);

                // ĐĐGgk (mid-term)
                Cell gkCell2 = row.createCell(colIndex++);
                gkCell2.setCellValue(hk2Score.getDdggk() != null ? hk2Score.getDdggk() : 0);
                gkCell2.setCellStyle(dataStyle);

                // ĐĐGck (final)
                Cell ckCell2 = row.createCell(colIndex++);
                ckCell2.setCellValue(hk2Score.getDdgck() != null ? hk2Score.getDdgck() : 0);
                ckCell2.setCellStyle(dataStyle);

                // TBm HK2
                Cell tbmCell2 = row.createCell(colIndex++);
                tbmCell2.setCellValue(hk2Score.getTbm() != null ? hk2Score.getTbm() : 0.0);
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
            if (hk1Score != null && hk1Score.getTbm() != null) {
                yearlyAvg += hk1Score.getTbm();
                // count++;
            }
            if (hk2Score != null && hk2Score.getTbm() != null) {
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
}
