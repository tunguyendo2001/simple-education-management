package com.example.studentapi.util;

public class FileValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Validates the size of the uploaded file.
     *
     * @param fileSize the size of the file in bytes
     * @return true if the file size is within the limit, false otherwise
     */
    public static boolean validateFileSize(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }

    /**
     * Validates the format of the uploaded file.
     *
     * @param fileName the name of the file
     * @return true if the file format is valid (CSV or XLSX), false otherwise
     */
    public static boolean validateFileFormat(String fileName) {
        return fileName.endsWith(".csv") || fileName.endsWith(".xlsx");
    }
}