package com.sandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sandbox.security.JwtAuthenticationEntryPoint;
import com.sandbox.security.JwtAuthenticationFilter;

/*
 * MAIN SPRING SECURITY CONFIGURATION
 *
 * Purpose:
 * This class defines the security rules of the application.
 *
 * It decides:
 * 1. Which APIs are public.
 * 2. Which APIs require authentication.
 * 3. Which ROLE can access which API.
 * 4. How unauthorized requests are handled.
 * 5. That JWT authentication is used instead of server-side sessions.
 */
@Configuration
public class SecurityConfig {

    /*
     * JwtAuthenticationFilter:
     * Reads the JWT token from incoming requests,
     * validates it, identifies the logged-in user,
     * and sets that user in Spring SecurityContext.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /*
     * JwtAuthenticationEntryPoint:
     * Handles requests where authentication is required
     * but the user is not properly authenticated.
     *
     * Example:
     * - Missing JWT
     * - Invalid JWT
     *
     * Usually returns HTTP 401 Unauthorized.
     */
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;


    /*
     * Constructor Dependency Injection.
     *
     * Spring automatically injects our JWT filter
     * and authentication entry point here.
     */
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    /*
     * SecurityFilterChain defines the complete security
     * configuration for incoming HTTP requests.
     *
     * Every request passes through this security chain
     * before reaching the controller.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

            /*
             * Disable CSRF protection.
             *
             * CSRF protection is mainly important for applications
             * using cookie/session-based authentication.
             *
             * Our REST API uses JWT Bearer tokens and is stateless,
             * so CSRF is disabled.
             */
            .csrf(csrf -> csrf.disable())


            /*
             * STATELESS means Spring Security will NOT create
             * or maintain an HTTP session for logged-in users.
             *
             * Every protected request must provide its JWT token.
             *
             * Client
             *   ↓
             * Authorization: Bearer <JWT>
             *   ↓
             * Server
             */
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )


            /*
             * Configure what happens when an unauthenticated user
             * tries to access a protected API.
             *
             * Our custom JwtAuthenticationEntryPoint will handle it.
             */
            .exceptionHandling(exception ->
                    exception.authenticationEntryPoint(
                            authenticationEntryPoint
                    )
            )


            /*
             * AUTHORIZATION RULES
             *
             * After authentication, Spring checks whether
             * the logged-in user's ROLE is allowed to access
             * the requested endpoint.
             */
            .authorizeHttpRequests(auth -> auth

                    /*
                     * PUBLIC ENDPOINTS
                     *
                     * No JWT token is required for these endpoints.
                     *
                     * /auth/login must be public because users
                     * need to log in before they can obtain a JWT.
                     */
                    .requestMatchers("/auth/login", "/error")
                    .permitAll()


                    /*
                     * SUPER ADMIN ENDPOINTS
                     *
                     * Only users having SUPER_ADMIN role
                     * can access organization and HR APIs.
                     *
                     * Examples:
                     * POST /organizations
                     * POST /hrs
                     *
                     * hasRole("SUPER_ADMIN") internally checks
                     * for authority "ROLE_SUPER_ADMIN".
                     */
                    .requestMatchers(
                            "/organizations/**",
                            "/hrs/**"
                    )
                    .hasRole("SUPER_ADMIN")


                    /*
                     * HR ENDPOINTS
                     *
                     * Only HR users can access candidate APIs.
                     *
                     * Examples:
                     * POST /candidates
                     * GET  /candidates
                     * GET  /candidates/3
                     */
                    .requestMatchers("/candidates/**")
                    .hasRole("HR")


                    /*
                     * Any endpoint not mentioned above
                     * still requires the user to be authenticated.
                     *
                     * The user needs a valid JWT, but there is
                     * no specific role requirement here.
                     */
                    .anyRequest()
                    .authenticated()
            )


            /*
             * Add our custom JWT filter BEFORE Spring Security's
             * UsernamePasswordAuthenticationFilter.
             *
             * This allows our filter to:
             *
             * 1. Read Authorization header
             * 2. Extract JWT
             * 3. Validate JWT
             * 4. Find the user
             * 5. Set Authentication in SecurityContext
             *
             * BEFORE Spring performs authorization checks.
             */
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );


        /*
         * build() creates the final SecurityFilterChain
         * that Spring Security will use for incoming requests.
         */
        return http.build();
    }
}