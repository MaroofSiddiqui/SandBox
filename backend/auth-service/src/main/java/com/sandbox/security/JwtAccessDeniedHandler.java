package com.sandbox.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JWT ACCESS DENIED HANDLER
 *
 * Purpose:
 * Handles authorization failures.
 *
 * This is called when:
 *
 * 1. User is successfully authenticated
 * 2. JWT is valid
 * 3. But user's role does NOT have permission
 *    to access the requested endpoint
 *
 * Example:
 *
 * HR → POST /organizations
 *
 * HR has valid JWT
 * but /organizations/** requires SUPER_ADMIN
 *
 * Result:
 * 403 Forbidden
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        // 403 means authenticated but not authorized.
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /*
         * Return a consistent JSON response instead
         * of Spring's default error response.
         */
        String json = """
                {
                    "status": 403,
                    "error": "Forbidden",
                    "message": "You do not have permission to access this resource",
                    "path": "%s"
                }
                """.formatted(request.getRequestURI());

        response.getWriter().write(json);
    }
}