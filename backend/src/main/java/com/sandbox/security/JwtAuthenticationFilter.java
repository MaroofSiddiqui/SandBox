package com.sandbox.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sandbox.entity.User;
import com.sandbox.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JWT AUTHENTICATION FILTER
 *
 * Purpose:
 * This filter checks the JWT token sent with every HTTP request
 * and authenticates the user if the token is valid.
 *
 * Protected requests normally contain:
 *
 * Authorization: Bearer <JWT_TOKEN>
 *
 * Main responsibilities:
 *
 * 1. Read the Authorization header
 * 2. Extract the JWT token
 * 3. Extract the user's email from the JWT
 * 4. Load the user from the database
 * 5. Validate the JWT
 * 6. Convert the user's role into a Spring Security authority
 * 7. Store the authenticated user in SecurityContext
 *
 * SecurityConfig places this filter before:
 *
 * UsernamePasswordAuthenticationFilter
 *
 * so JWT authentication happens before Spring checks
 * authorization rules.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /*
     * JwtService handles JWT-related operations such as:
     *
     * - Extracting email from token
     * - Checking token validity
     * - Checking token expiration
     */
    private final JwtService jwtService;

    /*
     * UserRepository is required because after extracting
     * the email from the JWT, we load the actual User
     * from the database.
     */
    private final UserRepository userRepository;


    /*
     * Constructor Dependency Injection.
     *
     * Spring automatically provides JwtService and
     * UserRepository when this filter is created.
     */
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    /*
     * doFilterInternal()
     *
     * Because this class extends OncePerRequestFilter,
     * this method runs once for each incoming HTTP request.
     *
     * Example:
     *
     * GET /candidates
     * Authorization: Bearer eyJ...
     *
     *          ↓
     *
     * JwtAuthenticationFilter
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        /*
         * Read the Authorization HTTP header.
         *
         * Expected format:
         *
         * Authorization: Bearer eyJhbGciOi...
         */
        String authHeader =
                request.getHeader("Authorization");


        /*
         * If the Authorization header:
         *
         * - Does not exist
         * OR
         * - Does not start with "Bearer "
         *
         * then there is no JWT for this filter to process.
         *
         * We simply continue the filter chain.
         *
         * IMPORTANT:
         * This does NOT automatically mean the request is allowed.
         *
         * SecurityConfig will later decide whether the endpoint
         * is public or requires authentication.
         */
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        /*
         * EXTRACT JWT FROM HEADER
         *
         * Header:
         *
         * "Bearer eyJhbGciOi..."
         *
         * "Bearer " contains 7 characters.
         *
         * substring(7) removes it and leaves only:
         *
         * "eyJhbGciOi..."
         */
        String token = authHeader.substring(7);


        /*
         * JWT operations can throw exceptions if the token is:
         *
         * - Expired
         * - Malformed
         * - Invalid
         * - Tampered with
         *
         * Therefore JWT processing is wrapped in try-catch.
         */
        try {

            /*
             * Extract the email stored inside the JWT.
             *
             * The email identifies which user the token
             * belongs to.
             */
            String email = jwtService.extractEmail(token);


            /*
             * Continue authentication only when:
             *
             * 1. An email was successfully extracted
             *
             * AND
             *
             * 2. SecurityContext does not already contain
             *    an authenticated user.
             *
             * This prevents us from authenticating the
             * same request again unnecessarily.
             */
            if (email != null
                    && SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {


                /*
                 * Find the actual User using the email
                 * extracted from the JWT.
                 *
                 * findByEmail() returns Optional<User>.
                 *
                 * orElse(null) means:
                 *
                 * User found     -> return User
                 * User not found -> return null
                 */
                User user = userRepository
                        .findByEmail(email)
                        .orElse(null);


                /*
                 * Authenticate only if:
                 *
                 * 1. User exists in the database
                 *
                 * AND
                 *
                 * 2. JWT is valid for this user.
                 */
                if (user != null
                        && jwtService.isTokenValid(token, user)) {


                    /*
                     * CONVERT ROLE INTO SPRING SECURITY AUTHORITY
                     *
                     * Our database stores:
                     *
                     * HR
                     * SUPER_ADMIN
                     * CANDIDATE
                     *
                     * Spring Security's hasRole("HR")
                     * expects an authority named:
                     *
                     * ROLE_HR
                     *
                     * Therefore:
                     *
                     * "ROLE_" + "HR"
                     *       ↓
                     * "ROLE_HR"
                     */
                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority(
                                    "ROLE_"
                                    + user.getRole().getName()
                            );


                    /*
                     * CREATE AUTHENTICATION OBJECT
                     *
                     * UsernamePasswordAuthenticationToken here
                     * represents an authenticated user inside
                     * Spring Security.
                     *
                     * Arguments:
                     *
                     * 1. principal
                     *    -> user
                     *
                     *    The actual logged-in User object.
                     *
                     * 2. credentials
                     *    -> null
                     *
                     *    Password is not required here because
                     *    authentication was established using JWT.
                     *
                     * 3. authorities
                     *    -> user's role/permissions
                     *
                     *    Example:
                     *    [ROLE_HR]
                     */
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    java.util.List.of(authority)
                            );


                    /*
                     * STORE AUTHENTICATION IN SECURITY CONTEXT
                     *
                     * This is the key step.
                     *
                     * It tells Spring Security:
                     *
                     * "This request has been authenticated,
                     *  and this is the logged-in user."
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);


                    /*
                     * Because User is stored as the principal,
                     * controllers can later access it using:
                     *
                     * Authentication authentication
                     *
                     * User currentUser =
                     *     (User) authentication.getPrincipal();
                     *
                     * This is exactly what CandidateController
                     * and OrganizationController are doing.
                     */
                }
            }

        } catch (Exception exception) {

            /*
             * If JWT parsing/validation fails because the token
             * is invalid, expired, malformed, or tampered with,
             * we do NOT authenticate the request.
             *
             * SecurityContext remains unauthenticated.
             *
             * The request continues through the filter chain.
             *
             * If the requested endpoint is protected,
             * Spring Security will later reject the request.
             */
        }


        /*
         * Continue processing the HTTP request through
         * the remaining Spring Security filters.
         *
         * Eventually the request may reach:
         *
         * Security authorization
         *       ↓
         * Controller
         *
         * if access is permitted.
         */
        filterChain.doFilter(request, response);
    }
}