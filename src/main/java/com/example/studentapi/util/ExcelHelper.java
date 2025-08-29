package com.example.studentapi.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.studentapi.model.Student;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "ID", "Name", "Gender", "Hometown", "Birthday" };
    static String SHEET = "Students";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Student> excelToStudents(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet(SHEET);
            List<Student> students = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // skip header
                }
                Student student = new Student();
                student.setId((long) row.getCell(0).getNumericCellValue());
                student.setName(row.getCell(1).getStringCellValue());
                student.setGender(row.getCell(2).getStringCellValue());
                student.setHometown(row.getCell(3).getStringCellValue());
                Date birthdayDate = row.getCell(4).getDateCellValue();
                student.setBirthday(birthdayDate == null ? null : birthdayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                students.add(student);
            }
            return students;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream studentsToExcel(List<Student> students) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET);
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < HEADERs.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERs[i]);
            }

            int rowIdx = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getGender());
                row.createCell(3).setCellValue(student.getHometown());
                row.createCell(4).setCellValue(student.getBirthday());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to export data to Excel file: " + e.getMessage());
        }
    }


}