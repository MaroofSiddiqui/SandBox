package com.sandbox.proctoring.security;

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
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        /*
         * If there is no Bearer token,
         * continue without authenticating.
         *
         * SecurityConfig will later decide whether
         * the requested endpoint requires authentication.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        try {

            /*
             * Validates:
             * - JWT signature
             * - expiration
             * - token structure
             */
            Claims claims =
                    jwtService.extractAllClaims(token);

            String email =
                    claims.getSubject();

            String role =
                    claims.get("role", String.class);

            /*
             * Auth Service stores:
             *
             * HR
             * CANDIDATE
             * SUPER_ADMIN
             *
             * Spring Security hasRole("HR")
             * expects ROLE_HR.
             */
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            /*
             * Keep all JWT claims available.
             *
             * Later we can retrieve:
             * userId
             * organizationId
             * role
             *
             * from authentication.getDetails().
             */
            authentication.setDetails(claims);

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception exception) {

            /*
             * Invalid/expired/tampered JWT.
             *
             * Temporarily print the exception while
             * integrating the microservices.
             */

            exception.printStackTrace();

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}