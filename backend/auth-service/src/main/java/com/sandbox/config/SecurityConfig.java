package com.sandbox.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sandbox.security.JwtAccessDeniedHandler;
import com.sandbox.security.JwtAuthenticationEntryPoint;
import com.sandbox.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtAccessDeniedHandler accessDeniedHandler;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			JwtAuthenticationEntryPoint authenticationEntryPoint, JwtAccessDeniedHandler accessDeniedHandler) {

		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// Enable CORS using the configuration below
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				.csrf(csrf -> csrf.disable())

				// JWT authentication is stateless
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Custom 401 and 403 responses
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))

				.authorizeHttpRequests(auth -> auth

// =========================
// PUBLIC APIs
// =========================
.requestMatchers(
        "/auth/login",
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/password/**",
        "/api/auth/email/**",
        "/error"
).permitAll()

						// =========================
						// SUBSCRIPTION VIEWING
						// HR + SUPER ADMIN
						// =========================
						.requestMatchers(HttpMethod.GET, "/admin/subscriptions", "/admin/subscriptions/**")
						.hasAnyRole("HR", "SUPER_ADMIN")

						// =========================
						// SUBSCRIPTION MANAGEMENT
						// SUPER ADMIN ONLY
						// =========================
						.requestMatchers("/admin/subscriptions/**").hasRole("SUPER_ADMIN")

						// =========================
						// SUPER ADMIN APIs
						// =========================
						.requestMatchers("/organizations/**", "/hrs/**").hasRole("SUPER_ADMIN")

						// =========================
						// HR APIs
						// =========================
						.requestMatchers("/candidates/**", "/hr/**").hasRole("HR")

						// =========================
						// PAYMENT OPERATIONS
						// HR
						// =========================

						// HR creates Razorpay order
						.requestMatchers(HttpMethod.POST, "/admin/payments/orders").hasRole("HR")

						// HR verifies completed Razorpay payment
						.requestMatchers(HttpMethod.POST, "/admin/payments/verify").hasRole("HR")

						// =========================
						// PAYMENT MONITORING
						// SUPER ADMIN
						// =========================

						// Super Admin can monitor all payments,
						// filter by organization and status.
						.requestMatchers(HttpMethod.GET, "/admin/payments", "/admin/payments/**").hasRole("SUPER_ADMIN")

						// Everything else requires JWT
						.anyRequest().authenticated())

				// Validate JWT before Spring authorization
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		// React development server
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));

		// HTTP methods frontend can use
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		// Allow Authorization and Content-Type headers
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		// Allow browser to read Authorization header if needed
		configuration.setExposedHeaders(List.of("Authorization"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// Apply CORS configuration to every API
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}