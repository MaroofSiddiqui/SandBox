package com.sandbox.assessment.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ============================================================
        // GET JWT FROM REQUEST
        // ============================================================

        String authorizationHeader =
                request.getHeader("Authorization");

        /*
         * No JWT supplied.
         *
         * Continue the filter chain.
         * Spring Security will decide whether authentication
         * is required for the requested endpoint.
         */
        if (
                authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        try {

            // ========================================================
            // VALIDATE AND READ JWT
            // ========================================================

            Claims claims =
                    jwtService.extractAllClaims(token);

            String email =
                    claims.getSubject();

            String role =
                    claims.get("role", String.class);

            // ========================================================
            // VALIDATE ROLE
            // ========================================================

            if (role == null || role.isBlank()) {

                System.out.println(
                        "[JWT] Role missing from token"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);
                return;
            }

            /*
             * JWT currently contains:
             *
             * role = CANDIDATE
             *
             * Spring Security hasRole("CANDIDATE") expects:
             *
             * ROLE_CANDIDATE
             *
             * Normalize the role safely.
             */

            String normalizedRole =
                    role.trim().toUpperCase();

            if (!normalizedRole.startsWith("ROLE_")) {

                normalizedRole =
                        "ROLE_" + normalizedRole;
            }

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            normalizedRole
                    );

            // ========================================================
            // CREATE AUTHENTICATION OBJECT
            // ========================================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            /*
             * Store the complete JWT claims.
             *
             * Controllers can later retrieve:
             *
             * userId
             * role
             * organizationId
             * etc.
             */
            authentication.setDetails(claims);

            // ========================================================
            // SET SECURITY CONTEXT
            // ========================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            // ========================================================
            // DEBUG INFORMATION
            // ========================================================

            System.out.println(
                    "[JWT] ========================================"
            );

            System.out.println(
                    "[JWT] Authenticated user: "
                            + email
            );

            System.out.println(
                    "[JWT] User ID: "
                            + claims.get("userId")
            );

            System.out.println(
                    "[JWT] Role from token: "
                            + role
            );

            System.out.println(
                    "[JWT] Normalized role: "
                            + normalizedRole
            );

            System.out.println(
                    "[JWT] Granted authority: "
                            + authority.getAuthority()
            );

            System.out.println(
                    "[JWT] Request: "
                            + request.getMethod()
                            + " "
                            + request.getRequestURI()
            );

            System.out.println(
                    "[JWT] ========================================"
            );

        } catch (Exception exception) {

            /*
             * JWT is invalid, expired, malformed,
             * or signature validation failed.
             */

            System.out.println(
                    "[JWT] Token validation failed: "
                            + exception.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        // ============================================================
        // CONTINUE FILTER CHAIN
        // ============================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}