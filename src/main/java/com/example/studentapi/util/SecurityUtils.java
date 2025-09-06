package com.example.studentapi.util;

import javax.servlet.http.HttpServletRequest;

public class SecurityUtils {
    
    public static Long getCurrentTeacherId(HttpServletRequest request) {
        Object teacherId = request.getAttribute("teacherId");
        if (teacherId instanceof Long) {
            return (Long) teacherId;
        }
        
        // Fallback to header
        String teacherIdHeader = request.getHeader("Teacher-Id");
        if (teacherIdHeader != null && !teacherIdHeader.isEmpty()) {
            try {
                return Long.parseLong(teacherIdHeader);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    public static boolean isCurrentTeacher(HttpServletRequest request, Long targetTeacherId) {
        Long currentTeacherId = getCurrentTeacherId(request);
        return currentTeacherId != null && currentTeacherId.equals(targetTeacherId);
    }
}