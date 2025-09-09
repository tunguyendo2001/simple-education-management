package com.example.studentapi.util;

import javax.servlet.http.HttpServletRequest;

public class SecurityUtils {
    
    /**
     * Extract teacher ID from the request.
     * This is a simplified implementation - in production, you would extract
     * the teacher ID from JWT token or session.
     */
    public static Long getCurrentTeacherId(HttpServletRequest request) {
        // First try to get from Authorization header (JWT token)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // In a real implementation, you would decode the JWT token here
            // For now, we'll fall back to the Teacher-Id header
        }
        
        // Fallback to Teacher-Id header
        String teacherIdHeader = request.getHeader("Teacher-Id");
        if (teacherIdHeader != null && !teacherIdHeader.trim().isEmpty()) {
            try {
                return Long.parseLong(teacherIdHeader.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Teacher-Id format: " + teacherIdHeader);
            }
        }
        
        throw new IllegalArgumentException("Teacher ID not found in request. Please provide Teacher-Id header or valid Authorization token.");
    }
    
    /**
     * Extract token from Authorization header
     */
    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * Check if request has valid authentication
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        try {
            Long teacherId = getCurrentTeacherId(request);
            return teacherId != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}