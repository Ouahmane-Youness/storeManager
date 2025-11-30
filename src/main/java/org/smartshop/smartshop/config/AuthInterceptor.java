package org.smartshop.smartshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.smartshop.smartshop.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.startsWith("/api/auth")) {
            return true;
        }
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized - Please login\"}");
            return false;
        }

        UserRole role = (UserRole) session.getAttribute("role");

        if (isAdminOnlyEndpoint(uri, method) && role != UserRole.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Forbidden - Admin access required\"}");
            return false;
        }

        return true;
    }

    private boolean isAdminOnlyEndpoint(String uri, String method) {
        if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE") || method.equals("PATCH")) {
            return true;
        }

        if (uri.contains("/users") || uri.contains("/promo-codes")) {
            return true;
        }

        return false;
    }

}
