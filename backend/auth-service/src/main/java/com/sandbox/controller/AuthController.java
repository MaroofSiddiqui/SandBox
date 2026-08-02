package com.sandbox.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.AuthResponse;
import com.sandbox.dto.LoginRequest;
import com.sandbox.dto.RegisterRequest;
import com.sandbox.service.AuthService;

import com.sandbox.exception.RateLimitExceededException;
import com.sandbox.security.ratelimit.LoginRateLimiter;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;

/*
 * AUTH CONTROLLER
 *
 * Purpose:
 * This controller handles authentication-related HTTP requests.
 *
 * Currently it provides:
 *
 * 1. POST /auth/login
 *    -> Authenticates a user and returns a JWT token.
 *
 * 2. GET /auth/profile
 *    -> Returns details of the currently authenticated user.
 *
 * This controller does NOT directly perform authentication logic.
 * That responsibility is delegated to AuthService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	/*
	 * AuthService contains the actual business logic for authentication, such as:
	 *
	 * - Finding the user by email - Checking the password - Checking account status
	 * - Generating the JWT token
	 */
	private final AuthService authService;
	private final LoginRateLimiter loginRateLimiter;

	/*
	 * Constructor Dependency Injection.
	 *
	 * Spring automatically provides the AuthService object when it creates this
	 * controller.
	 */
	public AuthController(
	        AuthService authService,
	        LoginRateLimiter loginRateLimiter) {

	    this.authService = authService;
	    this.loginRateLimiter = loginRateLimiter;
	}

	/*
	 * REGISTER
	 *
	 * Creates a new public candidate account.
	 *
	 * The role is NOT accepted from the frontend. AuthService automatically assigns
	 * CANDIDATE.
	 *
	 * After registration:
	 *
	 * - Password is BCrypt hashed - emailVerified = false - Verification token is
	 * generated - Verification email is sent
	 */
	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

		authService.register(request);

		return ResponseEntity.ok("Registration successful. Please verify your email.");
	}

	/*
	 * LOGIN ENDPOINT
	 *
	 * URL: POST /auth/login
	 *
	 * Example request:
	 *
	 * { "email": "admin@sandbox.com", "password": "Admin@123" }
	 *
	 * @PostMapping("/login") maps HTTP POST requests to /auth/login to this method.
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(
	        @Valid @RequestBody LoginRequest request,
	        HttpServletRequest httpRequest) {

	    /*
	     * Get the IP address of the client making
	     * the login request.
	     *
	     * For local development this will normally
	     * be 127.0.0.1 or 0:0:0:0:0:0:0:1.
	     */
	    String clientIp = httpRequest.getRemoteAddr();

	    /*
	     * Check whether this client has exceeded
	     * the allowed number of login requests.
	     */
	    if (!loginRateLimiter.isAllowed(clientIp)) {

	        throw new RateLimitExceededException(
	                "Too many login attempts. Please try again later."
	        );
	    }

	    /*
	     * Rate limit passed.
	     *
	     * Continue with normal authentication.
	     */
	    return ResponseEntity.ok(
	            authService.login(request)
	    );
	}

	/*
	 * PROFILE ENDPOINT
	 *
	 * URL: GET /auth/profile
	 *
	 * This is a protected endpoint.
	 *
	 * A valid JWT must be supplied:
	 *
	 * Authorization: Bearer <JWT>
	 *
	 * SecurityConfig requires authentication for this endpoint because it is not
	 * included in permitAll().
	 */
	@GetMapping("/profile")
	public ResponseEntity<?> profile(org.springframework.security.core.Authentication authentication) {

		/*
		 * Authentication represents the currently logged-in user.
		 *
		 * Before this controller executes:
		 *
		 * JWT request ↓ JwtAuthenticationFilter ↓ JWT validated ↓ User loaded ↓
		 * Authentication stored in SecurityContext ↓ Available here
		 *
		 * getPrincipal() returns the authenticated user's object.
		 *
		 * We cast it to our User entity because our JWT authentication mechanism stores
		 * the User as the principal.
		 */
		com.sandbox.entity.User user = (com.sandbox.entity.User) authentication.getPrincipal();

		/*
		 * Return only the user information that we want to expose.
		 *
		 * IMPORTANT: passwordHash is deliberately NOT returned.
		 *
		 * Map.of() creates a simple JSON-style key/value response.
		 */
		return ResponseEntity.ok(java.util.Map.of("id", user.getId(), "name", user.getName(), "email", user.getEmail(),
				"role", user.getRole().getName(), "status", user.getStatus()));
	}
}