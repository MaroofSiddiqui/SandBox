package com.sandbox.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JWT AUTHENTICATION ENTRY POINT
 *
 * Purpose:
 * This class handles requests where authentication is required,
 * but the request does not contain valid authentication.
 *
 * Typical situations:
 *
 * - JWT token is missing
 * - User tries to access a protected endpoint without logging in
 * - Authentication cannot be established
 *
 * In these situations, this class returns:
 *
 * HTTP 401 Unauthorized
 *
 * with a JSON error response.
 *
 * SecurityConfig connects this class using:
 *
 * .exceptionHandling(exception ->
 *     exception.authenticationEntryPoint(authenticationEntryPoint)
 * )
 */
@Component
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    /*
     * @Component
     *
     * Tells Spring to automatically create and manage
     * an object of this class as a Spring Bean.
     *
     * Because of this, it can be injected into SecurityConfig.
     */


    /*
     * AuthenticationEntryPoint
     *
     * This is a Spring Security interface used to define
     * what should happen when an unauthenticated request
     * tries to access a protected resource.
     *
     * We implement it so we can return our own JSON response
     * instead of Spring Security's default response.
     */


    /*
     * commence()
     *
     * This method is automatically called by Spring Security
     * when authentication is required but has not been established.
     *
     * We do NOT call this method ourselves.
     */
    @Override
    public void commence(

            /*
             * HttpServletRequest
             *
             * Represents the incoming HTTP request.
             *
             * We use it here to get the requested URL/path.
             *
             * Example:
             *
             * request.getRequestURI()
             *      ↓
             * "/candidates"
             */
            HttpServletRequest request,


            /*
             * HttpServletResponse
             *
             * Represents the HTTP response that will
             * be sent back to the client.
             *
             * We use it to set:
             *
             * - HTTP status
             * - Content type
             * - Character encoding
             * - JSON response body
             */
            HttpServletResponse response,


            /*
             * AuthenticationException
             *
             * Contains information about the authentication
             * failure that caused this entry point to run.
             *
             * We don't currently use authException directly,
             * but Spring Security provides it to this method.
             */
            AuthenticationException authException)

            /*
             * IOException is declared because writing data
             * to the HTTP response can potentially fail.
             */
            throws IOException {


        /*
         * Set HTTP status to:
         *
         * 401 Unauthorized
         *
         * SC_UNAUTHORIZED is the servlet constant for 401.
         */
        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );


        /*
         * Tell the client that the response body
         * contains JSON.
         *
         * HTTP header:
         *
         * Content-Type: application/json
         */
        response.setContentType("application/json");


        /*
         * Set response character encoding to UTF-8.
         *
         * This ensures characters are encoded correctly
         * in the JSON response.
         */
        response.setCharacterEncoding("UTF-8");


        /*
         * Build the JSON error response.
         *
         * Java text blocks (triple quotes) allow us to
         * write multi-line strings more cleanly.
         *
         * %s is a placeholder.
         *
         * .formatted(request.getRequestURI())
         *
         * replaces %s with the actual requested path.
         *
         * Example:
         *
         * Request:
         * GET /candidates
         *
         * Then:
         *
         * "path": "/candidates"
         */
        String json = """
                {
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "Authentication is required",
                    "path": "%s"
                }
                """.formatted(request.getRequestURI());


        /*
         * Write the JSON directly into the HTTP response body.
         *
         * getWriter()
         * -> Gets a writer for sending text data.
         *
         * write(json)
         * -> Sends our JSON error response to the client.
         */
        response.getWriter().write(json);
    }
}