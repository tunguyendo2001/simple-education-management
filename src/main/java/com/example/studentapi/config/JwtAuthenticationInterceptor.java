package com.example.studentapi.config;

import com.example.studentapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    // URLs that don't require authentication
    private final List<String> excludedPaths = Arrays.asList(
        "/api/auth/login",
        "/api/auth/validate",
        "/swagger-ui",
        "/api-docs",
        "/v3/api-docs",
        "/actuator"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Skip authentication for excluded paths
        String requestURI = request.getRequestURI();
        if (excludedPaths.stream().anyMatch(requestURI::startsWith)) {
            return true;
        }

        // Skip authentication for OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Extract JWT token from Authorization header
        String token = extractTokenFromRequest(request);
        
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"No authentication token provided\"}");
            response.setContentType("application/json");
            return false;
        }

        // // Validate token
        // if (!authService.validateToken(token)) {
        //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //     response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
        //     response.setContentType("application/json");
        //     return false;
        // }

        // // Add teacher ID to request attributes for use in controllers
        // Long teacherId = authService.getTeacherIdFromToken(token);
        // if (teacherId != null) {
        //     request.setAttribute("teacherId", teacherId);
        //     // Also add as header for backward compatibility
        //     request.setAttribute("Teacher-Id", teacherId.toString());
        // }

        String teacherId = "";
        request.setAttribute("teacherId", teacherId);
        // Also add as header for backward compatibility
        request.setAttribute("Teacher-Id", teacherId.toString());

        return true;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Try Authorization header first (Bearer token)
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Fallback to Teacher-Id header for simple implementation
        // (Not recommended for production - use JWT tokens)
        String teacherIdHeader = request.getHeader("Teacher-Id");
        if (teacherIdHeader != null && !teacherIdHeader.isEmpty()) {
            // This is a temporary fallback - in production, always use proper JWT
            return "simple-" + teacherIdHeader;
        }
        
        return null;
    }
}