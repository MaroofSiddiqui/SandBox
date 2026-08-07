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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		/*
		 * No JWT supplied.
		 *
		 * Continue the filter chain. Spring Security will later decide whether the
		 * requested endpoint requires authentication.
		 */
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(7);

		try {

			/*
			 * Parsing also validates:
			 *
			 * - JWT signature - token format - expiration
			 */
			Claims claims = jwtService.extractAllClaims(token);

			String email = claims.getSubject();

			String role = claims.get("role", String.class);

			/*
			 * Auth Service stores roles such as:
			 *
			 * HR CANDIDATE SUPER_ADMIN
			 *
			 * Spring Security expects ROLE_ prefix when using hasRole(...).
			 */
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
					List.of(authority));

			/*
			 * Keep useful Auth-Service JWT information available to Assessment
			 * controllers/services.
			 */
			authentication.setDetails(claims);

			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception exception) {

			System.out.println("JWT VALIDATION FAILED");
			exception.printStackTrace();

			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}
}